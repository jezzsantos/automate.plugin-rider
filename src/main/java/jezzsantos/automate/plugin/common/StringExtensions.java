package jezzsantos.automate.plugin.common;

import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringExtensions {

    public static String formatStructured(@NotNull String messageTemplate, @NotNull Map<String, Object> arguments) {

        var replacements = getMessageTemplateTokens(messageTemplate, arguments);
        if (replacements.isEmpty()) {
            return messageTemplate;
        }

        AtomicReference<String> message = new AtomicReference<>(messageTemplate);
        replacements.forEach((key, value) -> {

            var token = "{" + key + "}";

            String replacementString;
            var replacement = replacements.get(key);
            if (replacement == null) {
                replacementString = token;
            }
            else {

                if (replacement instanceof String stringValue) {
                    replacementString = stringValue;
                }
                else {
                    replacementString = replacement.toString();
                }

                if (replacementString == null || replacementString.isEmpty()) {
                    replacementString = token;
                }
            }

            message.set(message.get().replace(token, replacementString));
        });

        return message.get();
    }

    private static Map<String, Object> getMessageTemplateTokens(@NotNull String messageTemplate, @NotNull Map<String, Object> arguments) {

        @SuppressWarnings("RegExpRedundantEscape")
        var pattern = Pattern.compile("\\{(.+?)\\}");
        var expression = pattern.matcher(messageTemplate);

        var tokens = expression.results()
          .map(MatchResult::group)
          .distinct()
          .toList();
        if (tokens.size() == 0) {
            return Map.of();
        }

        return tokens.stream()
          .map(token -> token.substring(1, token.length() - 1))
          .map(name -> {
              var value = arguments.getOrDefault(name, null);
              return new AbstractMap.SimpleEntry<>(name, value);
          })
          .filter(entry -> entry.getValue() != null)
          .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }
}
