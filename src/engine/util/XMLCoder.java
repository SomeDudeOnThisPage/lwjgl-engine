package engine.util;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;

public final class XMLCoder
{
  /**
   * Returns the first {@link Element} by a given {@link String} id.
   * @param xml The XML {@link org.w3c.dom.Document} containing the data.
   * @param id The {@link String} id of the {@link Element}.
   * @return The {@link Element} with the given {@link String} id.
   * @throws IOException whenever a corresponding {@link Element} could not be found, or the data is corrupted.
   */
  public static Element get(@NotNull Element xml, @NotNull String id) throws IOException
  {
    NodeList nodes = xml.getElementsByTagName(id);

    if (nodes.getLength() == 0)
    {
      throw new IOException("could not find any element with tag name '" + id + "'");
    }

    Node node = nodes.item(0);
    if (node == null || node.getNodeType() != Node.ELEMENT_NODE)
    {
      throw new IOException("could not find any element with tag name '" + id + "'");
    }

    return (Element) node;
  }

  public static String gets(@NotNull Element xml, @NotNull String id) throws IOException
  {
    String content = XMLCoder.get(xml, id).getTextContent();
    if (content == null)
    {
      throw new IOException("text content of element '" + id + "' is missing or corrupted");
    }

    return content.trim();
  }

  public static String geta(@NotNull Element xml, @NotNull String id, @NotNull String attribute) throws IOException
  {
    if (XMLCoder.get(xml, id).getAttributes().getLength() <= 0)
    {
      throw new IOException("element '" + id + "' does not posess an attribute named '" + attribute + "'");
    }

    try
    {
      return XMLCoder.get(xml, id).getAttributes().getNamedItem(attribute).getTextContent().trim();
    }
    catch(Exception e)
    {
      throw new IOException("element '" + id + "' does not posess an attribute named '" + attribute + "', or the data is corrupted");
    }
  }
}