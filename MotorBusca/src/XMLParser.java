import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.io.IOException;

public class XMLParser {
    //Atributo
    private Document document; 

    //construtor
    public XMLParser(String caminhoArquivo) throws Exception{
        this.document = carregarXML(caminhoArquivo);
    }

    //carregando XML
    private Document carregarXML(String caminhoArquivo) throws ParserConfigurationException, SAXException, IOException{
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();                        //criando uma instancia DBF  
        dbf.setNamespaceAware(false);
        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
        return docBuilder.parse(new File(caminhoArquivo));
    }

    //fazendo busca e guardando por tags
    public NodeList getNodesByTagName(String tagName){
        return this.document.getElementsByTagName(tagName);
    }
}
