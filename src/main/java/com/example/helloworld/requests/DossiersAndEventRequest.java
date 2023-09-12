package com.example.helloworld.requests;

import java.util.List;
import lombok.Data;

@Data
public class DossiersAndEventRequest {

    private Long eventId;
    private List<Integer> dossiersList;

}
