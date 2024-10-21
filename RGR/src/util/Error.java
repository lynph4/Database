package util;

public sealed interface Error {
    
    record ValidationError(String description) implements Error {
    }

    record InsertError(String table) implements Error {
    }

    record RecordNotFound(String name) implements Error {
    }

    record ForeignKeyConstraintError(String field, String value) implements Error {
    }

    record DuplicateKeyError(String key) implements Error {
    }

    record OtherError(Throwable cause) implements Error {
    }

    record UnknownError() implements Error {
    }
}