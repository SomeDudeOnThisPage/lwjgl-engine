package engine.util;

import engine.Console;
import engine.asset.AssetManager;
import engine.asset.load.AssetFactory;
import org.joml.Vector3f;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

public class XMLUtil
{
  public static ArrayList<Element> elements(Element parent, String tag)
  {
    NodeList nodes = parent.getElementsByTagName(tag);
    ArrayList<Element> elements = new ArrayList<>();
    for (int i = 0; i < nodes.getLength(); i++)
    {
      Node node = nodes.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE)
      {
        elements.add((Element) node);
      }
    }
    return elements;
  }

  /**
   * Returns the first element with a given tag, should it exist! If not, this method will return {@code null}.
   * @param parent The parent XML element.
   * @param tag The tag to search for.
   * @return element or {@code null}.
   */
  public static Element element(Element parent, String tag)
  {
    NodeList nodes = parent.getElementsByTagName(tag);
    for (int i = 0; i < nodes.getLength(); i++)
    {
      Node node = nodes.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE)
      {
        return (Element) node;
      }
    }
    throw new AssetFactory.AssetLoadingException("failed to validate xml tag '" + tag + "' while loading asset");
  }

  public static int int1(String int1)
  {
    return Integer.parseInt(int1);
  }

  public static float float1(String float1)
  {
    return Float.parseFloat(float1);
  }

  public static Vector3f vec3(String vec3)
  {
    String[] parts = vec3.split(" ");
    if (parts.length != 3)
    {
      return new Vector3f(0.0f);
    }

    return new Vector3f(
      Float.parseFloat(parts[0]),
      Float.parseFloat(parts[1]),
      Float.parseFloat(parts[2])
    );
  }

  public static Document load(String path)
  {
    try
    {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();

      File file = new File(path);

      byte[] bytes = Files.readAllBytes(file.toPath());
      StringBuilder xmls = new StringBuilder(new String(bytes));
      xmls.insert(0, "<?xml version=\"1.0\"?>\n");

      // System.out.println(xmls.toString());

      ByteArrayInputStream input = new ByteArrayInputStream(xmls.toString().getBytes(StandardCharsets.UTF_8));
      return builder.parse(input);

    }
    catch (ParserConfigurationException | SAXException | IOException e)
    {
      Console.error(e);
    }
    return null;
  }

  public static String validate(Element root, String... tag)
  {
    for (String t : tag)
    {
      if (root.getElementsByTagName(t).getLength() <= 0)
      {
        // return cause
        return t;
      }
    }
    return null;
  }
}
