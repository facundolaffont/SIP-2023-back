package ar.edu.unlu.spgda.requests;

import lombok.Data;

@Data
public class TransferRegistersRequest {
    private Long sourceEventId;
    private Long targetEventId;
    private boolean forceOverwrite;
}
