package ch.dkrieger.bansystem.lib.command.defaults;

import ch.dkrieger.bansystem.lib.command.NetworkCommand;
import ch.dkrieger.bansystem.lib.command.NetworkCommandSender;

import java.util.List;

public class IpInfoCommand extends NetworkCommand {

    public IpInfoCommand() {
        super("ipinfo");
    }

    @Override
    public void onExecute(NetworkCommandSender sender, String[] args) {

    }
    @Override
    public List<String> onTabComplete(NetworkCommandSender sender, String[] args) {
        return null;
    }
}
