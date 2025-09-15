package com.example.proj;

import javafx.event.ActionEvent;
// Abstract Interface
public abstract class UserControllers extends Controllers {

    public abstract void logout(ActionEvent event);
    public abstract void switchform(ActionEvent e);
    public abstract void hiderespondwindow();
    public abstract void Setusersession(String user);

}
