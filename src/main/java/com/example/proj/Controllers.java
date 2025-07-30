package com.example.proj;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class Controllers {

    // Close the program immediately.
    public void exit(){System.exit(0);}

    // Return a string representing the current date and time in a custom format.
    public String getDate(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddHHmmss");
        return LocalDateTime.now().format(formatter);
    }
}
