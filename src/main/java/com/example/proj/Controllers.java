package com.example.proj;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class Controllers {

    public void exit(){System.exit(0);}

    public String getDate(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddHHmmss");
        return LocalDateTime.now().format(formatter);
    }
}
