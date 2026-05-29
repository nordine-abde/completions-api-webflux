package com.anordine.simplifier.webflux.ai.example;

import com.anordine.simplifier.webflux.ai.helper.tool.CompletionProvider;
import com.anordine.simplifier.webflux.ai.helper.tool.CompletionTool;
import com.fasterxml.jackson.annotation.JsonProperty;

@CompletionProvider
public class ExampleCompletionTools {

    @CompletionTool(
            name = "current_weather",
            description = "Get the current weather for a city."
    )
    public WeatherResult currentWeather(WeatherLookup lookup) {
        return new WeatherResult(lookup.city(), "sunny");
    }

    @CompletionTool(
            name = "support_hours",
            description = "Get customer support opening hours.",
            strict = false
    )
    public String supportHours() {
        return "09:00-17:00";
    }

    public record WeatherLookup(
            @JsonProperty(required = true) String city,
            String unit
    ) {
    }

    public record WeatherResult(String city, String condition) {
    }
}
