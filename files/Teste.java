public class Teste {
    public static void main(String[] args) {
        int x, a, b, c, i, contador;
        double f;
        boolean condition;
        boolean condition2;
        String name;

        x = 10;
        name = "FuLaNo";
        System.out.println(name);

        condition = true;
        condition2 = false;
        b = x * 5;
        c = x / 5;
        a = (b + c) % 7;
        f = x / 5;
        System.out.println(a);
        System.out.println(f);

        if (x > 2) {
            x = x + 1;
            System.out.println(x);
        } else {
            System.out.println(0);
        }

        if (x < 2) {
            System.out.println(1);
        }

        while (x > 7) {
            System.out.println(x);
            x = x - 1;
        }

        for (i = 0; i <= x; i = i + 1) {
            System.out.println(i);
        }

        for (i = 0; i < 3; i++) {
            System.out.println(i);
        }

        for (i = 3; i >= 0; i--) {
            System.out.println(i);
        }

        contador = 12;
        switch (contador) {
            case 10:
                System.out.println(10);
                break;
            case 12:
                System.out.println(12);
                break;
            default:
                System.out.println(0);
        }
    }
}
