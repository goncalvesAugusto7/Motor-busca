import java.util.HashMap;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        //paths: 
            /*
                C:\\Users\\Augusto\\Downloads\\conteudo_modificado.xml
                C:\\Users\\Augusto\\Downloads\\verbetesWikipedia.xml
             */


        //menuInterativo();
        preProcessamento();

    }

    // Rotina de pre processamento
    public static void preProcessamento() throws Exception{
        try (Scanner scanner = new Scanner(System.in)) {
                
            //pegando a localizacao do arquivo
            System.out.println("Digite o caminho do arquivo que desenha analisar:");
            XMLParser xml = new XMLParser(scanner.nextLine());

                //limpando o terminal
                System.out.print("\033[H\033[2J"); 
                System.out.flush();

            //montando mapa pre processado
            PreProcess preProcess = new PreProcess(xml);
            HashMap<Integer,HashMap<String,Double>> mapa = preProcess.getMapaPreProcessado();
                    //System.out.println(mapa);

            //loop de execucao
            while(true){
                
                System.out.println("Digite sua pesquisa:");
                String entrada = scanner.nextLine();
                //System.out.print("Saindo do cash? ");
                if(!preProcess.getCash().containsKey(entrada)){ //a esntrada nao esta no cash, procurando no mapa pre processado

                    //System.out.print("Nao");
                    String saida = "\n"+preProcess.ordenarPorValores(preProcess.pesquisarPalavras(mapa, entrada));
                    //System.out.println(preProcess.pesquisarPalavras(mapa, entrada));
                    System.out.println(saida);
                    preProcess.putCash(entrada, saida);
                }else{  //a entrada ja esta no cash, retornando o valor ja registrado

                    //System.out.print("Sim");
                    System.out.println(preProcess.getCash().get(entrada));
                }
            }
        } 
    }


    //Rotina classica
    public static void menuInterativo() throws Exception{

        try (Scanner scanner = new Scanner(System.in)) {

            

            //pegando a localizacao do arquivo
            System.out.println("Digite o caminho do arquivo que desenha analisar:");
            XMLParser xml = new XMLParser(scanner.nextLine());

            String selec = scanner.nextLine();

            //limpando o terminal
            System.out.print("\033[H\033[2J"); 
            System.out.flush();


            if(selec.equals("1")){
                System.out.println("Digite:");
                String entrada = scanner.nextLine();
                TitleAnalyzer analyzer = new TitleAnalyzer(xml);
                analyzer.analisarTitulos(entrada);

            }else if(selec.equals("2")){
                System.out.println("Digite:");
                String entrada = scanner.nextLine();
                TextAnalyzer analyzer = new TextAnalyzer(xml);
                analyzer.analisarTextos(entrada);
                
            }else if(selec.equals("3")){
                System.out.println("Digite:");
                String entrada = scanner.nextLine();
                TextAnalyzer analyzer = new TextAnalyzer(xml);
                analyzer.analisarTextosPercentagem(entrada);
                
            }else{
                System.out.println("Opcao invalida");
            }
    }
    }
}