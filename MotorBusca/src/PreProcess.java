import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PreProcess {
    // Atributo
    private XMLParser xml;
    private HashMap<Integer, String> mapaTitulos = new HashMap<>();
    private HashMap<String,String> cash = new HashMap<String,String>();

    //get
    public HashMap<String,String> getCash(){
        return this.cash;
    }

    // Construtor
    public PreProcess(XMLParser xml) {
        this.xml = xml;
    }

    //--Metodos--

    // Fazendo pre-processamento
    public HashMap<Integer, HashMap<String, Double>> getMapaPreProcessado() {
        // criando lista de nodes
        NodeList nodeListPages = this.xml.getNodesByTagName("page");
        NodeList nodeListTitles = this.xml.getNodesByTagName("title");
        NodeList nodeListTexts = this.xml.getNodesByTagName("text");

        // mapa final com as páginas e seus mapas com as palavras e as porcentagens
        HashMap<Integer, HashMap<String, Double>> mapaPages = new HashMap<>();

        // loop sobre as páginas
        for (int i = 0; i < nodeListPages.getLength(); i++) {
            // pegando o item do node analisado no loop
            Node nodePage = nodeListPages.item(i);
            Node nodeTitle = nodeListTitles.item(i);
            Node nodeText = nodeListTexts.item(i);

            // verificando se os itens do node são elementos válidos
            if (nodePage.getNodeType() == Node.ELEMENT_NODE && nodeTitle.getNodeType() == Node.ELEMENT_NODE && nodeText.getNodeType() == Node.ELEMENT_NODE) {
                // cast o conteúdo do node para um Element (necessário para analisar as strings)
                Element elementPage = (Element) nodePage;
                Element elementTitle = (Element) nodeTitle;
                Element elementText = (Element) nodeText;

                String page = elementPage.getTextContent().trim();
                String title = elementTitle.getTextContent();
                String text = elementText.getTextContent();

                // palavras na página
                String pages[] = page.split("\\s+");
                String palavrasTitulo[] = title.split("\\s+");
                String palavrasTexto[] = text.split("\\s+");

                // filtrando palavras por tamanho (mínimo 4 caracteres)
                ArrayList<String> palavrasFiltradas = new ArrayList<>();
                for (String palavra : palavrasTitulo) {
                    if (palavra.length() >= 4) {
                        palavrasFiltradas.add(palavra);
                    }
                }
                for (String palavra : palavrasTexto) {
                    if (palavra.length() >= 4) {
                        palavrasFiltradas.add(palavra);
                    }
                }

                // transformando a lista em um array
                String[] palavrasTotais = palavrasFiltradas.toArray(new String[0]);

                // pegando o total de palavras no texto (title + text) para calcular a porcentagem
                int totalNrPalavras = palavrasTotais.length;

                // mapa de ocorrências por página
                HashMap<String, Integer> mapaOcorrencias = new HashMap<>();
                // mapa de porcentagens de palavras por página
                HashMap<String, Double> mapPorcentagem = new HashMap<>();

                // loop sobre o título
                for (String palavra : palavrasTitulo) {
                    if(palavra.length() > 4)
                        mapaOcorrencias.put(palavra.toLowerCase(), mapaOcorrencias.getOrDefault(palavra, 0) + 1);
                }
                // adicionando porcentagens do título no total
                for (String palavra : mapaOcorrencias.keySet()) {
                    double porcentagem = (mapaOcorrencias.get(palavra) / (double) totalNrPalavras) * 100;
                    mapPorcentagem.put(palavra.toLowerCase(), porcentagem * 10);
                }

                // limpando o mapa de ocorrências para conter apenas as próximas ocorrências dos títulos
                mapaOcorrencias.clear();

                // loop sobre o texto
                for (String palavra : palavrasTexto) {
                    if(palavra.length() > 4)
                        mapaOcorrencias.put(palavra.toLowerCase(), mapaOcorrencias.getOrDefault(palavra, 0) + 1);
                }
                // adicionando porcentagens do texto no total
                for (String palavra : mapaOcorrencias.keySet()) {
                    double porcentagem = (mapaOcorrencias.get(palavra) / (double) totalNrPalavras) * 100;
                    mapPorcentagem.put(palavra.toLowerCase(), porcentagem);
                }

                // Adicionando o conteúdo da página salvo no mapa total de páginas
                mapaPages.put(Integer.parseInt(pages[0]), mapPorcentagem);
                mapaTitulos.put(Integer.parseInt(pages[0]), title);
            }

        }

        // retornando o mapa final
        return mapaPages;
    }

    // Pesquisando palavra
    public HashMap<Integer, Double> pesquisarPalavras(HashMap<Integer, HashMap<String, Double>> mapa, String entrada) {
        // mapa de saída
        HashMap<Integer, Double> mapaValorPorPage = new HashMap<>();

        if(entrada.length() > 4){
                //salvando porcentagens
            double ocorrencias = 0.0;

            //transformando a plavra em um vetor para verificar o tamanho
            String vetEntrada[] = entrada.split("\\s+");

            // loop que itera sobre as páginas
            for (Map.Entry<Integer, HashMap<String, Double>> page : mapa.entrySet()) {
                int nrPage = page.getKey();
                HashMap<String, Double> mapaPalavras = page.getValue();

                //verificando se a entrada eh de uma ou duas palavras
                if(vetEntrada.length < 2){ //o usuario pesquisou apenas por uma palavra
                    ocorrencias = 0.0;
                    // verificando se a palavra está na página
                    if (mapaPalavras.containsKey(vetEntrada[0].toLowerCase())) {
                        ocorrencias += mapaPalavras.get(entrada.toLowerCase());
                    }

                }else{  //o usuario procurou mais de uma palavra
                    ocorrencias = 0.0;
                    if (mapaPalavras.containsKey(vetEntrada[0].toLowerCase())){
                        //System.out.println(vetEntrada[0]);
                        ocorrencias += mapaPalavras.get(vetEntrada[0].toLowerCase());
                    }
                    
                    if(mapaPalavras.containsKey(vetEntrada[1].toLowerCase())){
                        //System.out.println(vetEntrada[1]);
                        ocorrencias += mapaPalavras.get(vetEntrada[1].toLowerCase());
                    }
                }

                // colocando a página e o valor no mapa da saída
                if(ocorrencias != 0.0)
                    mapaValorPorPage.put(nrPage, ocorrencias);
            }
        } 

        //System.out.println(mapaValorPorPage);
        return mapaValorPorPage;
    }

    // Imprimindo saída do cliente
    public String ordenarPorValores(HashMap<Integer, Double> mapa) {
        // Converter o HashMap para uma lista de entradas
        List<Map.Entry<Integer, Double>> listaDeEntradas = new ArrayList<>(mapa.entrySet());

        // Ordenar a lista de entradas pelos valores
        Collections.sort(listaDeEntradas, new Comparator<Map.Entry<Integer, Double>>() {
            @Override
            public int compare(Map.Entry<Integer, Double> entrada1, Map.Entry<Integer, Double> entrada2) {
                return entrada2.getValue().compareTo(entrada1.getValue());
            }
        });

        // Construir uma string com o mapa ordenado
        StringBuilder resultado = new StringBuilder();
        int i = 0;
        if(listaDeEntradas.size() > 0){
            for (Map.Entry<Integer, Double> entrada : listaDeEntradas) {
                resultado.append("\t("+(i+1)+") ").append(obterTituloDaPagina(entrada.getKey())+"\n");
                if (i == 5) {
                    break;
                }
                i++;
            }
        }else{
            resultado.append("\tDesculpe... nao encontramos bons resultados para sua pesquisa ;-;\n");
            //System.err.println(listaDeEntradas);
        }

        // Retornar a string resultante
        return resultado.toString();
    }

    // Método para obter o título de uma página específica
    public String obterTituloDaPagina(int numeroPagina) {
        return mapaTitulos.getOrDefault(numeroPagina, "Título não encontrado");
    }

    // cash de itens ja pesquisados
    public boolean putCash(String chave, String retorno){
        if(!this.cash.containsKey(chave)){
            this.cash.put(chave, retorno);
            return true;
        }
        return false;
    }
}