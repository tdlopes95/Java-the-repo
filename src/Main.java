import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.PrintWriter;

public class Main {
    static String titulo = "";
    static PrintWriter escrita = null;

    public static void main(String[] args) throws FileNotFoundException {
        String validador="";
        String mood;
        int escolha;

        Scanner ler = new Scanner(System.in);
        validador = intro();

        while(!validador.equalsIgnoreCase("N")){


            System.out.println("Como deseja proceder? 1-Inserção Manual  2-Através de Ficheiro");
            do { escolha = ler.nextInt(); } while (escolha != 1 && escolha != 2);
            ler.nextLine();

            int[][] moodData;
            String nomeFicheiro = "";

            if(escolha == 1){
                moodData = insertMood();

                //printwriter
                nomeFicheiro = (titulo+"_analysis.txt");
                escrita = new PrintWriter(new File("output/" + nomeFicheiro));

            }else{
                System.out.println("Insira o nome do moodmap que deseja ler: (ex:mood_nov.txt)");
                mood = ler.nextLine();
                moodData = lerMood(mood);           // a)

                //printwriter
                nomeFicheiro = mood.replace(".txt", "_analysis.txt");
                escrita = new PrintWriter(new File("output/" + nomeFicheiro));
            }

            visualizarMoodMap(moodData);                                     // b)
            double[] mediaDia = mediaPorDia(moodData);                       // c)
            mediaPorPessoa(moodData);                                        // d)
            diasMaiorHumor(moodData, mediaDia);                              // e)
            percentagemNiveis(moodData);                                     // f)
            int[] maxConsecutivoPorPessoa = transtornoEmocional(moodData);   // g)
            graficoPorPessoa(moodData);                                      // h)
            pessoasTerapia(moodData, maxConsecutivoPorPessoa);               // i)
            humorSemelhante(moodData);                                       // j)


            escrita.close();
            System.out.println("\n");
            System.out.printf("Analysis saved to output/%s\n", nomeFicheiro);

            System.out.println("Deseja Continuar? (S/N)");
            String repMain = ler.next();
            ler.nextLine();
            if(repMain.equalsIgnoreCase("S")){
                validador=intro();
            }else{
                break;
            }
        }

    }

    /*****************************************************
     ************** j) Humor semelhante ******************
     *****************************************************/
    private static void humorSemelhante(int[][] moodData) {
        int pessoas = moodData.length;
        int dias = moodData[0].length;

        int maxIguais = 0;
        int pessoa_1 = -1;
        int pessoa_2 = -1;

        // comparação entre as varias pessoas
        for (int i = 0; i < pessoas; i++) {
            for (int k = i + 1; k < pessoas; k++) {
                int diasIguais = 0;
                for (int j = 0; j < dias; j++) {
                    if (moodData[i][j] == moodData[k][j]) {
                        diasIguais++;
                    }
                }
                // só guarda valor se for detectado algum valor igual, e guarda as pessoas com menor identificação primeiro ( se for igual mantem)
                if (diasIguais > maxIguais) {
                    maxIguais = diasIguais;
                    pessoa_1 = i;
                    pessoa_2 = k;
                }
            }
        }

        System.out.print("\nj) People with the most similar moods: ");
        escrita.print("\nj) People with the most similar moods: ");
        if (maxIguais == 0) {
            System.out.print("there aren't people with similar moods.");
            escrita.print("there aren't people with similar moods.");
        } else {
            System.out.printf("(Person #%d and Person #%d have the same mood on %d days)", pessoa_1 + 1, pessoa_2 + 1, maxIguais);
            escrita.printf("(Person #%d and Person #%d have the same mood on %d days)", pessoa_1 + 1, pessoa_2 + 1, maxIguais);
        }
    }
    /*****************************************************
     ************** i) Pessoas com terapia ***************
     *****************************************************/
    private static void pessoasTerapia(int[][] moodData, int[] maxConsecutivoPorPessoa) {
        int pessoas = moodData.length;
        boolean validador = false;

        System.out.println("i) Recommended therapy:");
        escrita.println("i) Recommended therapy:");

        for (int i = 0; i < pessoas; i++) {
            if (maxConsecutivoPorPessoa[i] >= 2 && maxConsecutivoPorPessoa[i] <= 4) {
                System.out.printf("Person #%d : Listen to music\n", i + 1);
                escrita.printf("Person #%d : Listen to music\n", i + 1);
                validador = true;
            } else if (maxConsecutivoPorPessoa[i] >= 5) {
                System.out.printf("Person #%d : Psychological support\n", i + 1);
                escrita.printf("Person #%d : Psychological support\n", i + 1);
                validador = true;
            }
        }

        if (!validador) {
            System.out.println("Not needed. You have a happy business!");
            escrita.println("Not needed. You have a happy business!");
        }
    }
    /*****************************************************
     ************** h) Gráfico por pessoa ****************
     *****************************************************/
    private static void graficoPorPessoa(int[][] moodData) {
        int pessoas = moodData.length;
        int dias = moodData[0].length;

        System.out.println("\nh) People's Mood Level Charts:");
        escrita.println("\nh) People's Mood Level Charts:");

        for (int i = 0; i < pessoas; i++) {
            // verificacao dos limites para uma pessoa
            int minMood = 5;
            int maxMood = 1;
            for (int j = 0; j < dias; j++) {
                if (moodData[i][j] < minMood) {
                    minMood = moodData[i][j];
                }
                if (moodData[i][j] > maxMood) {
                    maxMood = moodData[i][j];
                }
            }

            System.out.printf("Person #%d:%n", i + 1);
            escrita.printf("Person #%d:%n", i + 1);

            // impressao
            for (int mood = maxMood; mood >= minMood; mood--) {
                System.out.printf("%4d |", mood);
                escrita.printf("%4d |", mood);
                for (int j = 0; j < dias; j++) {
                    if (moodData[i][j] == mood) {
                        System.out.print("*");
                        escrita.print("*");
                    } else {
                        System.out.print(" ");
                        escrita.print(" ");
                    }
                }
                System.out.println();
                escrita.println();
            }

            // layout de baixo
            System.out.print("Mood +");
            escrita.print("Mood +");
            for (int j = 0; j < dias; j++) {
                System.out.print("-");
                escrita.print("-");
            }
            System.out.println();
            escrita.println();

            // dias
            System.out.print("     ");
            escrita.print("     ");
            for (int j = 0; j <= dias; j++) {
                if (j % 5 == 0) {
                    System.out.printf("%-5d", j);
                    escrita.printf("%-5d", j);
                }
            }
            System.out.println("\n");
            escrita.println("\n");
        }
    }
    /*****************************************************
     ************** g) Transtorno emocional **************
     *****************************************************/
    private static int[] transtornoEmocional(int[][] moodData) {
        int pessoas = moodData.length;
        int dias = moodData[0].length;
        boolean validador = false;
        int[] maxConsecutivoPorPessoa = new int[pessoas];

        System.out.println("\ng) People with emotional disorders:");
        escrita.println("\ng) People with emotional disorders:");

        for (int i = 0; i < pessoas; i++) {
            int maxConsecutivo = 0;
            int consecutivoAtual = 0;

            for (int j = 0; j < dias; j++) {
                if (moodData[i][j] <= 2) {
                    consecutivoAtual++;
                    if (consecutivoAtual > maxConsecutivo) {
                        maxConsecutivo = consecutivoAtual;
                    }
                } else {
                    consecutivoAtual = 0; // reset
                }
            }

            maxConsecutivoPorPessoa[i] = maxConsecutivo;

            if (maxConsecutivo >= 2) {
                System.out.printf("Person #%d : %d consecutive days%n", i+1, maxConsecutivo);
                escrita.printf("Person #%d : %d consecutive days%n", i+1, maxConsecutivo);
                validador = true;
            }

        }

        if (!validador) {
            System.out.println("No one! You have a happy company!");
            escrita.println("No one! You have a happy company!");
        }
        return maxConsecutivoPorPessoa;
    }
    /*****************************************************
     ************** f) Percentagem de níveis *************
     *****************************************************/
    private static void percentagemNiveis(int[][] moodData) {
        int pessoas = moodData.length;
        int dias = moodData[0].length;
        int total = pessoas * dias;
        double percentagem;


        int[] contador = new int[5];
        for (int i = 0; i < pessoas; i++) {
            for (int j = 0; j < dias; j++) {
                contador[moodData[i][j]-1]++; // -1 porque moodData[i][j] tem valores de 1 a 5
            }
        }

        System.out.println("\nf) Percentage of mood levels:");
        escrita.println("\nf) Percentage of mood levels:");
        for (int mood = 4; mood >= 0; mood--) {
            percentagem = (double) contador[mood] / total * 100;
            System.out.printf("Mood #%d: %.1f%%%n", mood+1, percentagem);
            escrita.printf("Mood #%d: %.1f%%%n", mood+1, percentagem);
        }
    }
    /*****************************************************
     ************** e) Dias com maior humor **************
     *****************************************************/
    private static void diasMaiorHumor(int[][] moodData, double[] mediaDia) {
        int dias = moodData[0].length;
        double maior = mediaDia[0];

        for (int i = 1; i < dias; i++) {
            if (mediaDia[i] > maior) {
                maior = mediaDia[i];
            }
        }

        System.out.printf("\ne) Days with the highest average mood (%.1f)  :  ", maior);
        escrita.printf("\ne) Days with the highest average mood (%.1f)  :  ", maior);
        // teste para ver se há mais dias com a maior média
        for (int i = 0; i < dias; i++) {
            if (mediaDia[i] == maior) {
                System.out.printf("%d ", i);
                escrita.printf("%d ", i);
            }
        }
        System.out.println();
        escrita.println();
    }
    /*****************************************************
     ************** d) Média por pessoa ******************
     *****************************************************/
    private static void mediaPorPessoa(int[][] moodData) {
        int pessoas = moodData.length;
        int dias = moodData[0].length;
        double media;

        System.out.println("\nd) Average of each person's mood: ");
        escrita.println("\nd) Average of each person's mood: ");
        for (int i = 0; i < pessoas; i++) {
            int soma = 0;

            for (int j = 0; j < dias; j++) {
                soma = soma + moodData[i][j];
            }
            media = (double) soma / dias;
            System.out.printf("Person #%-3d: %.1f%n", i+1, media);
            escrita.printf("Person #%-3d: %.1f%n", i+1, media);
        }
    }

    /*****************************************************
     ************** c) Média por dia *********************
     *****************************************************/
    private static double[] mediaPorDia(int[][] moodData) {
        int pessoas = moodData.length;
        int dias = moodData[0].length;
        double[] mediaDiaria = new double [dias];

        for (int j = 0; j < dias; j++) {       // j=dias / i=pessoas
            int soma = 0;
            for (int i = 0; i < pessoas; i++) {
                soma = soma + moodData[i][j];
            }
            mediaDiaria[j] = (double) soma / pessoas;

        }

        System.out.println("\nc) Average mood each day:");
        escrita.println("\nc) Average mood each day:");
        System.out.printf("%-11s:", "day"); //cabeçalho
        escrita.printf("%-11s:", "day");
        for (int j = 0; j < dias; j++) {
            System.out.printf("%4d", j+1);
            escrita.printf("%4d", j+1);
        }
        System.out.println();
        escrita.println();


        System.out.print("-----------|"); //separador
        escrita.print("-----------|");
        for (int j = 0; j < dias; j++) {
            System.out.print("---|");
            escrita.print("---|");
        }
        System.out.println();
        escrita.println();


        System.out.printf("%-12s", "mood"); //medias
        escrita.printf("%-12s", "mood");
        for (int j = 0; j < dias; j++) {
            System.out.printf("%4.1f", mediaDiaria[j]);
            escrita.printf("%4.1f", mediaDiaria[j]);
        }
        System.out.println();
        escrita.println();

        return mediaDiaria;
    }

    /*****************************************************
     ************** b) Visualizar MoodMap ****************
     *****************************************************/
    private static void visualizarMoodMap(int[][] moodData) {
        int pessoas = moodData.length;
        int dias = moodData[0].length;

        System.out.printf("\n %s \n",titulo); //titulo
        escrita.printf("\n %s \n",titulo);
        System.out.println("b) Mood (level/day(person)");
        escrita.println("b) Mood (level/day(person)");

        System.out.printf("%-11s:", "day"); //cabeçalho
        escrita.printf("%-11s:", "day");
        for (int j = 0; j < dias; j++) {
            System.out.printf("%4d", j+1);
            escrita.printf("%4d", j+1);
        }
        System.out.println();
        escrita.println();

        System.out.print("-----------|"); //separador superior
        escrita.print("-----------|");
        for (int j = 0; j < dias; j++) {
            System.out.print("---|");
            escrita.print("---|");
        }
        System.out.println();
        escrita.println();

        for (int i = 0; i < pessoas; i++) {
            System.out.printf("Person #%-3d:", i+1);
            escrita.printf("Person #%-3d:", i+1);
            for (int j = 0; j < dias; j++) {
                System.out.printf("%4d", moodData[i][j]);
                escrita.printf("%4d", moodData[i][j]);
            }
            System.out.println();
            escrita.println();
        }

    }
    /*****************************************************
     ****** a.2) Inserir Manualmente MoodMap *************
     *****************************************************/
    private static int [][] insertMood() {
        int pessoas,dias;
        Scanner ler = new Scanner(System.in);


        System.out.println("Digite o nome do Departamento/Equipa que está a analisar: (sem espaços)");
        titulo = ler.next();
        System.out.println("Digite o número de pessoas em análise: ");
        do {
            while (!ler.hasNextInt()) {
                System.out.println("Erro: Deve inserir um número inteiro!");
                ler.next();
            }
            pessoas = ler.nextInt();
        } while (pessoas < 1 );
        System.out.println("Digite o número de dias em análise: ");
        do {
            while (!ler.hasNextInt()) {
                System.out.println("Erro: Deve inserir um número inteiro!");
                ler.next();
            }
            dias = ler.nextInt();
        } while (dias < 1 );


        int[][] moodData = new int[pessoas][dias];

        System.out.println("Digite os valores de Mood para cada dia (pessoa a pessoa)");
        for (int i = 0; i < pessoas; i++) {
            System.out.println("Pessoa nº"+(i+1)+": ");
            for (int j = 0; j < dias; j++) {
                do { moodData[i][j] = ler.nextInt(); } while (moodData[i][j] < 1 && moodData[i][j] > 5);
                moodData[i][j] = ler.nextInt();
            }
        }

        return moodData;
    }
    /*****************************************************
     ************** a) Ler MoodMap ***********************
     *****************************************************/
    private static int[][] lerMood(String mood) throws FileNotFoundException {
        File moodMap = new File("input/" + mood);
        Scanner leitorFicheiro = new Scanner(moodMap);

        titulo = leitorFicheiro.nextLine();

        int pessoas = leitorFicheiro.nextInt();
        int dias = leitorFicheiro.nextInt();

        int[][] moodData = new int[pessoas][dias];
        for (int i = 0; i < pessoas; i++) {
            for (int j = 0; j < dias; j++) {
                moodData[i][j] = leitorFicheiro.nextInt();
            }
        }

        leitorFicheiro.close();
        return moodData;
    }
    /*****************************************************
     ********************* INTRO *************************
     *****************************************************/
    public static String intro() {
        String valid;
        Scanner ler = new Scanner(System.in);

        System.out.print("""
                Leitor de MoodMap.
                Deseja Iniciar? (S/N)
                """);
        do { valid = ler.next(); } while (!valid.equalsIgnoreCase("S") && !valid.equalsIgnoreCase("N"));
        return valid;
    }
}