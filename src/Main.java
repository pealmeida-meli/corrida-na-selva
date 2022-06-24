import java.text.Normalizer;
import java.util.*;
import java.util.stream.Stream;

public class Main {
    private static final String PEQUENO = "pequeno";
    private static final String MEDIO = "medio";
    private static final String AVANCADO = "avancado";

    private static final Scanner scanner = new Scanner(System.in);
    private static final Map<String, List<Participant>> table = new HashMap<>();

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

        var all = Stream.concat(table.get(PEQUENO).stream(), table.get(MEDIO).stream());
        all = Stream.concat(all, table.get(AVANCADO).stream());
        var found = all.filter(x -> x.getRg().equals(participant.getRg())).findFirst();

        if (found.isEmpty())
            table.get(category).add(participant);
        else
            throw new Exception("Usuário já cadastrado");
    }

    private static void listParticipants() throws Exception {
        var category = readCategory();
        var list = table.get(category);

        System.out.printf("Categoria %s\n\n", category);

        for (var p : list) {
            System.out.printf("ID: %d\n", p.getId());
            System.out.printf("Nome: %s\n", p.getFirstName());
            System.out.printf("Sobrenome: %s\n", p.getLastName());
            System.out.printf("Idade: %d\n", p.getAge());
            System.out.printf("RG: %s\n", p.getRg());
            System.out.printf("Telefone: %s\n", p.getPhone());
            System.out.printf("Telefone de emergência: %s\n", p.getEmergencyPhone());
            System.out.printf("Grupo sanguíneo: %s\n", p.getBloodGroup());
            System.out.println();
        }
    }

    private static void cancelSubscription() throws Exception {
        var category = readCategory();

        System.out.print("Digite o ID do participante: ");
        var id = Integer.parseInt(scanner.nextLine());

        var found = table.get(category)
                .stream()
                .filter(x -> x.getId() == id)
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

    private static Participant readParticipant() {
        var participant = new Participant();

        System.out.print("Digite o nome: ");
        participant.setFirstName(scanner.nextLine());

        System.out.print("Digite o sobrenome: ");
        participant.setLastName(scanner.nextLine());

        System.out.print("Digite o RG: ");
        participant.setRg(scanner.nextLine());

        System.out.print("Digite a idade: ");
        participant.setAge(Integer.parseInt(scanner.nextLine()));

        System.out.print("Digite o número de celular: ");
        participant.setPhone(scanner.nextLine());

        System.out.print("Digite o número de emergência: ");
        participant.setEmergencyPhone(scanner.nextLine());

        System.out.print("Digite o grupo sanguíneo: ");
        participant.setBloodGroup(scanner.nextLine());

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
