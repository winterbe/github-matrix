package com.winterbe.matrix;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Benjamin Winterberg
 */
public class GithubCollector {

    private final RestTemplate restTemplate = new RestTemplate();

    private final String apiToken;

    public GithubCollector(String apiToken) {
        this.apiToken = apiToken;
    }

    public List<Drop> collect() {
        String url = "https://api.github.com/events";
        ResponseEntity<String> response = get(url);
        return parseResponse(response.getBody());
    }

    private List<Drop> parseResponse(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setDateFormat(new ISO8601DateFormat());
            ArrayNode eventNodes = (ArrayNode) objectMapper.readTree(responseBody);
            List<Drop> drops = new ArrayList<>();
            for (JsonNode eventNode : eventNodes) {
                String type = eventNode.get("type").asText();
                if ("PushEvent".equals(type)) {
                    parsePushEvent(eventNode, objectMapper)
                            .forEach(drops::add);
                }
            }
            return drops;
        }
        catch (IOException e) {
            throw new RuntimeException("could not parse github api result: " + responseBody, e);
        }
    }

    private List<Drop> parsePushEvent(JsonNode eventNode, ObjectMapper objectMapper) throws IOException {
        long id = eventNode.get("id").asLong();
        Date timestamp = objectMapper.convertValue(eventNode.get("created_at"), Date.class);
        String user = eventNode.get("actor").get("login").asText();
        String repository = eventNode.get("repo").get("name").asText();
        ArrayNode commitNodes = (ArrayNode) eventNode.get("payload").get("commits");

        List<Drop> drops = createDrops(commitNodes);
        for (Drop drop : drops) {
            drop.setId(id);
            drop.setUser(user);
            drop.setRepository(repository);
            drop.setTimestamp(timestamp);
        }
        return drops;
    }

    private List<Drop> createDrops(ArrayNode commitNodes) throws IOException {
        List<Drop> drops = new ArrayList<>();
        for (JsonNode commitNode : commitNodes) {
            String url = commitNode.get("url").asText();

            ResponseEntity<String> response = get(url);
            if (response.getStatusCode() != HttpStatus.OK) {
                continue;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            JsonNode fileNodes = jsonNode.get("files");

            for (int i = 0; i < fileNodes.size() && i < 5; i++) {
                JsonNode fileNode = fileNodes.get(i);
                parseFileNode(fileNode)
                        .ifPresent(c -> {
                            String htmlUrl = jsonNode.get("html_url").asText();
                            c.setUrl(htmlUrl);
                            drops.add(c);
                        });
            }
        }
        return drops;
    }

    private Optional<Drop> parseFileNode(JsonNode fileNode) {
        JsonNode patchNode = fileNode.get("patch");
        if (patchNode != null) {
            String patch = parsePatch(patchNode.asText());
            if (patch.length() > 10) {
                Drop drop = new Drop();
                drop.setCode(patch);
                drop.setFileSha(fileNode.get("sha").asText());
                return Optional.of(drop);
            }
        }
        return Optional.empty();
    }

    private String parsePatch(String patch) {
        String result = Arrays.stream(patch.split("\n"))
                .filter(line -> line.startsWith("+"))
                .map(line -> StringUtils.removeStart(line, "+"))
                .map(line -> StringUtils.replace(line, "\t", " "))
                .map(StringUtils::normalizeSpace)
                .filter(s -> !StringUtils.startsWithAny(s, "//", "/*", "*", "#"))   // strip comments
                .collect(Collectors.joining(" "));
        return StringUtils.abbreviate(result, 255);
    }

    private ResponseEntity<String> get(String url) {
        HttpHeaders headers = createAuthHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, request, String.class);
    }

    private HttpHeaders createAuthHeaders() {
        String plainCreds = apiToken + ":x-oauth-basic";
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);
        return headers;
    }
}