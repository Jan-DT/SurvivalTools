package nl.jandt;

import io.wispforest.owo.config.annotation.Config;

@Config(name = "config", wrapperName = "SrvConfig")
public class SrvConfigModel {
    public boolean infoEnabled = true;
    public String infoMessage = "Dit is het infobericht";
}
