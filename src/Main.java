import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    private static int nextId = 1;

    private static final String PEQUENO = "pequeno";
    private static final String MEDIO = "medio";
    private static final String AVANCADO = "avancado";

    private static final Scanner scanner = new Scanner(System.in);
    private static final Map<String, List<Map<String, Object>>> table = new HashMap<>();

    static {
        table.put(PEQUENO, new ArrayList<>());
        table.put(MEDIO, new ArrayList<>());
        table.put(AVANCADO, new ArrayList<>());
    }

    public static void main(String[] args) throws Exception {
        while (true) {
            System.out.print(">>> ");
            var command = scanner.nextLine();

            if (command.equals("help") || command.equals("ajuda") || command.equals("?")) {
                System.out.println("Comandos válidos: cadastrar, listar, cancelar, valor, ajuda");
                continue;
            }

            switch (command) {
                case "cadastrar":
                    registerParticipant();
                    break;
                case "listar":
                    listParticipants();
                    break;
                case "cancelar":
                    cancelSubscription();
                    break;
                case "valor":
                    calculateFee();
                    break;
                default:
                    System.out.println("Comando inválido");
                    break;
            }
        }
    }

    private static void registerParticipant() throws Exception {
        var category = readCategory();
        var participant = readParticipant();

        var stream = Stream.concat(table.get(PEQUENO).stream(), table.get(MEDIO).stream());
        var all = Stream.concat(stream, table.get(AVANCADO).stream()).collect(Collectors.toList());

        var found = all
                .stream()
                .filter(x -> x.get("rg").equals(participant.get("rg")))
                .findFirst();

        if (found.isEmpty()) {
            participant.put("id", nextId++);
            table.get(category).add(participant);
        }
        else {
            throw new Exception("Usuário já cadastrado");
        }
    }

    private static void listParticipants() throws Exception {
        var category = readCategory();
        var list = table.get(category);

        System.out.printf("Categoria %s\n\n", category);

        for (var p : list) {
            System.out.printf("ID: %d\n", (int) p.get("id"));
            System.out.printf("Nome: %s\n", p.get("firstName"));
            System.out.printf("Sobrenome: %s\n", p.get("lastName"));
            System.out.printf("Idade: %d\n", (int) p.get("age"));
            System.out.printf("RG: %s\n", p.get("rg"));
            System.out.printf("Telefone: %s\n", p.get("phone"));
            System.out.printf("Telefone de emergência: %s\n", p.get("emergencyPhone"));
            System.out.printf("Grupo sanguíneo: %s\n", p.get("bloodGroup"));
            System.out.println();
        }
    }

    private static void cancelSubscription() throws Exception {
        var category = readCategory();

        System.out.print("Digite o ID do participante: ");
        var id = Integer.parseInt(scanner.nextLine());

        var found = table.get(category)
                .stream()
                .filter(x -> (int) x.get("id") == id)
                .findFirst();

        if (found.isEmpty()) {
            throw new Exception("Participante não encontrado");
        }

        table.get(category).remove(found.get());
    }

    private static void calculateFee() throws Exception {
        var category = readCategory();

        System.out.print("Digite a idade: ");
        var age = Integer.parseInt(scanner.nextLine());

        System.out.printf("O valor a ser pago é: R$ %d\n\n", getFee(category, age));
    }

    private static String readCategory() throws Exception {
        var category = "";
        System.out.print("Digite a categoria: ");
        category = scanner.nextLine();

        category = stripAccents(category.toLowerCase());
        if (!categoryIsValid(category))
            throw new Exception("Categoria inválida");

        return category;
    }

    private static HashMap<String, Object> readParticipant() {
        var participant = new HashMap<String, Object>();

        System.out.print("Digite o nome: ");
        participant.put("firstName", scanner.nextLine());

        System.out.print("Digite o sobrenome: ");
        participant.put("lastName", scanner.nextLine());

        System.out.print("Digite o RG: ");
        participant.put("rg", scanner.nextLine());

        System.out.print("Digite a idade: ");
        participant.put("age", Integer.parseInt(scanner.nextLine()));

        System.out.print("Digite o número de celular: ");
        participant.put("phone", scanner.nextLine());

        System.out.print("Digite o número de emergência: ");
        participant.put("emergencyPhone", scanner.nextLine());

        System.out.print("Digite o grupo sanguíneo: ");
        participant.put("bloodGroup", scanner.nextLine());

        return participant;
    }

    private static int getFee(String category, int age) throws Exception {
        var fee = 0;

        switch (category) {
            case PEQUENO:
                if (age < 18)
                    fee = 1300;
                else
                    fee = 1500;
                break;
            case MEDIO:
                if (age < 18)
                    fee = 2000;
                else
                    fee = 2300;
                break;
            case AVANCADO:
                if (age < 18)
                    throw new Exception("Não pode");
                else
                    fee = 2800;
                break;
        }

        return fee;
    }

    private static boolean categoryIsValid(String category) {
        return category.equals(PEQUENO) || category.equals(MEDIO) || category.equals(AVANCADO);
    }

    private static String stripAccents(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("\\p{InCombiningDiacriticalMarks}", "");
        return s;
    }
}
