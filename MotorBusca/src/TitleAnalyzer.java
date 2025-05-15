import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TitleAnalyzer {
    //Atributo
    private final XMLParser xml;

    //Construtor
    public TitleAnalyzer(XMLParser xml){
        this.xml = xml;
    }

    //analisando titulos
    public void analisarTitulos(String entrada){
        NodeList nodeList = this.xml.getNodesByTagName("title");
        int nOcorrencias = 0;
        String listagemTitulos = "";

        for(int i = 0; i < nodeList.getLength(); i++){
            Node node = nodeList.item(i);

            //verificando se o node analisado eh um elemento valido
            if(node.getNodeType() == Node.ELEMENT_NODE){
                Element element = (Element)node;
                String titulo = element.getTextContent();

                //verificando se a entrada esta presente no titulo analisado 
                if(titulo.toLowerCase().matches(".*\\b"+entrada.toLowerCase()+"\\b.*")){
                    nOcorrencias++;
                    listagemTitulos += "("+nOcorrencias+") -> "+titulo+"\n";
                }
            }
        }
        //saida principal
        System.out.println("A palavra '"+entrada+"' esta presente em "+nOcorrencias+" titulos:\n"+listagemTitulos);
    }
}
