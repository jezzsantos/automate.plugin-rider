package jezzsantos.automate.plugin.common;

import org.junit.jupiter.api.Test;

public class TryTest {

    @Test
    public void whenTryAndThrows_ThenReturns() {
        Try.safely(() -> {
            throw new Exception("amessage");
        });
    }

    @Test
    public void whenTry_TheReturns() {
        Try.safely(() -> {});
    }
}
