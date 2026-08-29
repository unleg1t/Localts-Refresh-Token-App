package org.localts.exception;

public class AuthenticationException extends RuntimeException {
   private final boolean retryable;

   public AuthenticationException(String message) {
      this(message, false);
   }

   public AuthenticationException(String message, boolean retryable) {
      super(message);
      this.retryable = retryable;
   }

   public boolean isRetryable() {
      return this.retryable;
   }
}
