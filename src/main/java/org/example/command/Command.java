package org.example.command;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.Callable;

@Getter
@Setter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "commandType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ScanCommand.class, name = "SCAN"),
        @JsonSubTypes.Type(value = MapCommand.class, name = "MAP"),
        @JsonSubTypes.Type(value = ExportMapCommand.class, name = "EXPORTMAP")
})
public abstract class Command implements Callable<String> {

    private Status status = Status.PENDING;
    private CommandType commandType;
    private String jobName;

}
