package andrewpeltier.wbl_arduino;

import android.support.v4.app.Fragment;

import andrewpeltier.wbl_arduino.fragments.CommandsFragment;
import andrewpeltier.wbl_arduino.fragments.HelpFragment;
import andrewpeltier.wbl_arduino.fragments.HomeFragment;

public class GetFragment
{
    public static Fragment get(String prevFrag)
    {
        switch(prevFrag)
        {
            case "HomeFragment":
                return new HomeFragment();
            case "HelpFragment":
                return new HelpFragment();
            case "CommandsFragment":
                return new CommandsFragment();
        }
        return null;
    }
}
