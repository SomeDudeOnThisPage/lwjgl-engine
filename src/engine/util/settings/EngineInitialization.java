package engine.util.settings;

import org.ini4j.Profile;
import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class EngineInitialization
{
  public static void initialize()
  {
    // initialize logger
    // todo

    // initialize settings
    try
    {
      Wini ini = new Wini(new File("engine.ini"));
      Set<Map.Entry<String, Profile.Section>> sections = ini.entrySet();

      for (Map.Entry<String, Profile.Section> e : sections)
      {
        Profile.Section section = e.getValue();

        Set<Map.Entry<String, String>> values = section.entrySet();
        for (Map.Entry<String, String> e2 : values)
        {
          // register setting in settings (duh)
          char typedef = e2.getKey().charAt(0);
          Class type;
          Object data;
          switch (typedef)
          {
            case 'b':
              type = Boolean.class;
              data = Boolean.valueOf(e2.getValue());
              break;
            case 'i':
              type = Integer.class;
              data = Integer.valueOf(e2.getValue());
              break;
            case 'f':
              type = Float.class;
              data = Float.valueOf(e2.getValue());
              break;
            case 'd':
              System.err.println("double not allowed, narrowing to float");
              type = Float.class;
              data = Float.valueOf(e2.getValue());
              break;
            default:
              type = String.class;
              data = e2.getValue();
              break;
          }

          EngineSetting setting = new EngineSetting(e2.getValue(), type);
          setting.set(data);
          Settings.register(e2.getKey().substring(1), setting);
        }
      }
    }
    catch (IOException e)
    {
      System.err.println("could not find required initialization files");
      e.printStackTrace();
      System.exit(-1);
    }
  }
}