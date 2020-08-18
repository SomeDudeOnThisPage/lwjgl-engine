package engine.asset;

import engine.Engine;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashSet;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class ReloadWatchService
{
  private WatchService service;

  private HashSet<String> watched;

  public void update()
  {
    WatchKey key = this.service.poll();
    if (key != null)
    {
      Path dir = (Path) key.watchable();

      for (WatchEvent<?> event : key.pollEvents())
      {
        Path path = dir.resolve((Path) event.context());

        for (String w : watched)
        {
          System.err.println(w + " " + path.toString());
          System.err.println(w.equals(path.toString()));
        }

        if (Files.exists(path) && this.watched.contains(path.toString()))
        {
          System.out.println("Event kind : " + event.kind() + " - File : " + path);
          AssetManager.getInstance().load(path.toString());
        }
      }

      key.reset();
    }
  }

  public void register(String path)
  {
    if (!this.watched.contains(path))
    {
      Path p = FileSystems.getDefault().getPath(new File("" + path).getParent());

      try
      {
        p.register(this.service, ENTRY_MODIFY);
        this.watched.add(Paths.get(path).toString());
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }


  }

  public ReloadWatchService()
  {
    try
    {
      this.service = FileSystems.getDefault().newWatchService();
      this.watched = new HashSet<>();
    }
    catch(Exception e)
    {
      Engine.Log.error("failed to instantiate watch service - " + e);
    }
  }
}
