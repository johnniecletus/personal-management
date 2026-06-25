package com.aj.personal.projects.management.service.support;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class SavingsClusterTemplateRegistry {

    private final Map<String, Map<String, Integer>> templates = new HashMap<>();

    public SavingsClusterTemplateRegistry() {
        registerTemplate(
                "salary",
                Map.of(
                        normalize("emergency savings"), 30,
                        normalize("tithe"), 10,
                        normalize("children savings"), 20,
                        normalize("main savings"), 15
                )
        );

        registerTemplate(
                "gift",
                Map.of(
                        normalize("emergency savings"), 40,
                        normalize("children savings"), 30,
                        normalize("main savings"), 20,
                        normalize("house savings"), 10
                )
        );
    }

    public Integer resolveSuggestedPercentage(String clusterName, String itemName) {
        Map<String, Integer> template = templates.get(normalize(clusterName));

        if (template == null) {
            return null;
        }

        return template.get(normalize(itemName));
    }

    private void registerTemplate(String clusterName, Map<String, Integer> itemPercentages) {
        templates.put(normalize(clusterName), itemPercentages);
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }

        return value.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "");
    }
}
