import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextAnalyzer {
    //Atributo
    private XMLParser xml;

    //Construtor
    public TextAnalyzer(XMLParser xml){
        this.xml = xml;
    }

    //analisando textos
    public void analisarTextos(String entrada){
        //criando listas de nodes
        NodeList nodeListTitles = this.xml.getNodesByTagName("title");
        NodeList nodeListTexts = this.xml.getNodesByTagName("text");

        //mapa de pesos e ocorrencias
        HashMap<String,Integer> mapaPesos = new HashMap<String,Integer>();
        int nOcorrencias = 0;

        //loop sobre os nodes
        for(int i = 0; i < nodeListTitles.getLength(); i++){
            //pegando o item do node analisado no loop
            Node nodeTitle = nodeListTitles.item(i);
            Node nodeText = nodeListTexts.item(i);

            //verificando se os itens do node sao elementos validos
            if(nodeTitle.getNodeType() == Node.ELEMENT_NODE && nodeText.getNodeType() == Node.ELEMENT_NODE){
                //casting o conteudo do node para um Element (necessario para analisar as strings)
                Element elementTitle = (Element)nodeTitle;
                Element elementtext = (Element)nodeText;

                String titulo = elementTitle.getTextContent();
                String texto = elementtext.getTextContent();

                //verificando a ocorrencia da entrada no titulo analisado
                if(titulo.toLowerCase().matches(".*\\b"+entrada.toLowerCase()+"\\b.*")){
                    nOcorrencias++;
                    int nOcorrenciasTexto = contarOcorrenciastexto(entrada, texto);
                    mapaPesos.put(titulo,nOcorrenciasTexto);
                }
            }
        }
        printSort(mapaPesos, entrada, nOcorrencias);
    }

    //metodo para contar as ocorrencias de uma entrada em um texto
    public int contarOcorrenciastexto(String entrada, String texto){
        String palavras[] = texto.split("\\s+");
        int cont = 0;
        
        for (String palavra : palavras) {
            if(palavra.equalsIgnoreCase(entrada)){
                cont++;
            }
        }

        return cont;
    }

    //metodo para contar as ocorrencias de uma entrada e a porcentagem dela no texto
    public double porcentagemOcorrenciastexto(String entrada, String texto){
        String[] palavras = texto.split("\\s+");
        int cont = 0;
        double percentagem = 0.0;
        
        for (String palavra : palavras) {
            if(palavra.equalsIgnoreCase(entrada)){
                cont++;
            }
        }
        if(cont > 0){
            percentagem = ((double)cont / palavras.length) * 100;
        }
        System.out.println(cont + " / " + palavras.length + " = " + percentagem);
        return percentagem;
    }
    

    //imprimindo resultados ordenados
    public static void printSort(HashMap<String,Integer> mapaPesos, String entrada, int nOcorrenciasText){
        List<Map.Entry<String,Integer>> listaPesos = new ArrayList<>(mapaPesos.entrySet()); //criando uma lista para adicionar o conteudo do mapa
        listaPesos.sort((e1,e2)->e2.getValue().compareTo(e1.getValue())); //sort

        System.out.println(nOcorrenciasText+" da palavra '"+entrada+"':");


        int indice = 0;
        for (Map.Entry<String,Integer> entry : listaPesos) {
            indice++;
            System.out.println("("+indice+")-> "+entry.getKey()+"\n\t"+entry.getValue()+" ocorrencia(s)");
        }
    }

    //imprimindo resultados ordenados com percentagem
    
    public void printSortPercento(HashMap<String,Double> mapaPesos, String entrada){
        List<Map.Entry<String,Double>> listaPesos = new ArrayList<>(mapaPesos.entrySet()); //criando uma lista para adicionar o conteudo do mapa
        listaPesos.sort((e1,e2)->e2.getValue().compareTo(e1.getValue())); //sort

        //System.out.println(pOcorrenciasTexto+"% palavra '"+entrada+"':");


        int indice = 0;
        for (Map.Entry<String,Double> entry : listaPesos) {
            indice++;
            System.out.println("\t("+indice+")-> "+entry.getKey()+"\n\t"+entry.getValue()+" ocorrencia(s)");
        }
    }

    //analisando textos com peso de ocorrencias com titulo
    public void analisarTextosPercentagem(String entrada){
        //criando listas de nodes
        NodeList nodeListTitles = this.xml.getNodesByTagName("title");
        NodeList nodeListTexts = this.xml.getNodesByTagName("text");

        //mapa de pesos e ocorrencias
        HashMap<String,Double> mapaPesos = new HashMap<String,Double>();
        double pOcorrenciasTexto = 0.0f;

        //loop sobre os nodes
        for(int i = 0; i < nodeListTitles.getLength(); i++){
            //pegando o item do node analisado no loop
            Node nodeTitle = nodeListTitles.item(i);
            Node nodeText = nodeListTexts.item(i);

            //verificando se os itens do node sao elementos validos
            if(nodeTitle.getNodeType() == Node.ELEMENT_NODE && nodeText.getNodeType() == Node.ELEMENT_NODE){
                //casting o conteudo do node para um Element (necessario para analisar as strings)
                Element elementTitle = (Element)nodeTitle;
                Element elementtext = (Element)nodeText;

                String titulo = elementTitle.getTextContent();
                String texto = elementtext.getTextContent();

                //verificando a ocorrencia da entrada no titulo analisado
                if(titulo.toLowerCase().matches(".*\\b"+entrada.toLowerCase()+"\\b.*")){
                    pOcorrenciasTexto = porcentagemOcorrenciastexto(entrada, texto);
                    String palavrasTitulo[] = titulo.split("\\s+");
                    for(String palavra: palavrasTitulo){
                        if (palavra.equalsIgnoreCase(entrada)) {
                            pOcorrenciasTexto += 1;
                        }
                    }
                    mapaPesos.put(titulo,pOcorrenciasTexto);

                }else{
                    //verificando se a entrada está contida no texto
                    if(contarOcorrenciastexto(entrada,texto) >= 1){
                        
                    }
                }
            }
        }
        printSortPercento(mapaPesos, entrada);
    }

    // Fazendo pre-processamento
    public HashMap<Integer,HashMap<String,Double>> preProcessarTextos(){
        //criando lista de node
        NodeList nodeListPages = this.xml.getNodesByTagName("page");
        NodeList nodeListTitles = this.xml.getNodesByTagName("title");
        NodeList nodeListTexts = this.xml.getNodesByTagName("text");

        //mapa de ocorrencias por pagina
        HashMap<String,Integer> mapaOcorrencias = new HashMap<String,Integer>();
        //mapa de porcentagens de palavras por pagina
        HashMap<String,Double> mapPorcentagem = new HashMap<String,Double>();

        //mapa final com as paginas e seus mapas com as palavras e as porcentagens
        HashMap<Integer,HashMap<String,Double>> mapaPages = new HashMap<Integer,HashMap<String,Double>>();


        //loop sobre as paginas
        for(int i = 0; i < nodeListPages.getLength(); i++){
            //pegando o item do node analisado no loop
            Node nodePage = nodeListPages.item(i);
            Node nodeTitle = nodeListTitles.item(i);
            Node nodeText = nodeListTexts.item(i);

            //verificando se os itens do node sao elementos validos
            if(nodePage.getNodeType() == Node.ELEMENT_NODE && nodeTitle.getNodeType() == Node.ELEMENT_NODE && nodeText.getNodeType() == Node.ELEMENT_NODE){
                //casting o conteudo do node para um Element (necessario para analisar as strings)
                Element elementPage = (Element)nodePage;
                Element elementTitle = (Element)nodeTitle;
                Element elementText = (Element)nodeText;

                String page = elementPage.getTextContent();
                String title = elementTitle.getTextContent();
                String text = elementText.getTextContent();

                //palavras na pagina
                String palavrasTitulo[] = title.split("\\s+");
                String palavrasTexto[] = text.split("\\s+");

                //pegando o total de palavras no texto (title + text) para calcular a porcentagem
                int totalPalavras = palavrasTitulo.length + palavrasTexto.length;
                
                //loop sobre o titulo
                for(String palavra: palavrasTitulo){
                    if(mapaOcorrencias.containsKey(palavra)){
                        mapaOcorrencias.put(palavra, mapaOcorrencias.getOrDefault(palavra, 0) + 1);                   
                    }else{
                        mapaOcorrencias.put(palavra, 1);
                    }
                    
                }
                //adicionando porcentagens do titulo no total
                for(String palavra: mapaOcorrencias.keySet()){
                    double porcentagem = (mapaOcorrencias.get(palavra) / (double)totalPalavras) * 100;
                    mapPorcentagem.put(palavra, porcentagem * 1.1);
                }
                
                //lipando o mapa para conter apenas as proximas ocorrencias dos titulos
                mapaOcorrencias.clear();

                //loop sobre o texto
                for(String palavra: palavrasTexto){
                    if(mapaOcorrencias.containsKey(palavra)){
                        mapaOcorrencias.put(palavra, mapaOcorrencias.getOrDefault(palavra, 0) + 1);                   
                    }else{
                        mapaOcorrencias.put(palavra, 1);
                    }
                }
                //adicionando porcentagens do texto no total
                for(String palavra: mapaOcorrencias.keySet()){
                    double porcentagem = (mapaOcorrencias.get(palavra) / (double)totalPalavras) * 100;
                    mapPorcentagem.put(palavra, porcentagem);
                }

                //Adicionando o conteudo da pagina salvado no mapa total de paginas
                mapaPages.put(Integer.parseInt(page), mapPorcentagem);
            }

        }

        //retornando o mapa final
        return mapaPages;
    }
}
