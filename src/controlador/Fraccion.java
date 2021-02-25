package controlador;

public class Fraccion {

    private int numerador;
    private int denominador;

    public Fraccion() {
    }

    /*Metodo constructor de la clase, recibe dos parametros (numerador, denominador)*/
    public Fraccion(int numerador, int denominador) {
        if (denominador != 0) {
            this.numerador = numerador;
            this.denominador = denominador;
        }
    }

    /*Metodos get y set */
    public int getNumerador() {
        return numerador;
    }

    public void setNumerador(int numerador) {
        this.numerador = numerador;
    }

    public int getDenominador() {
        return denominador;
    }

    public void setDenominador(int denominador) {
        this.denominador = denominador;
    }

    /*Operaciones Basicas: SUMA, RESTA, MULTIPLICACION Y DIVISION*/
    // f1 = 4/3   f2 = 5/2      (5*3) + (2*4)  /  3*2  = 15 + 8 /  6  = 23/6   
    public Fraccion sumar(Fraccion f) {
        return new Fraccion(f.getNumerador() * getDenominador() + f.getDenominador() * getNumerador(),
                f.getDenominador() * getDenominador());
    }

    // f1 = 4/3   f2 = 5/2      (4*2) - (3*5)  /  3*2  =  8 - 15 / 6  = -7/6   
    public Fraccion restar(Fraccion f) {
        return new Fraccion((getNumerador() * f.getDenominador()) - (getDenominador() * f.getNumerador()),
                f.getDenominador() * getDenominador());
    }

    // f1 = 4/3   f2 = 5/2     5*4 / 2*3  =  20/6 
    public Fraccion multiplicar(Fraccion f) {
        return new Fraccion(f.getNumerador() * getNumerador(), f.getDenominador() * getDenominador());
    }

    // f1 = 4/3   f2 = 5/2     4*2 / 5*3  =  8/15
    public Fraccion dividir(Fraccion f) {
        return new Fraccion(getNumerador() * f.getDenominador(), f.getNumerador() * getDenominador());
    }

    /*Metodos para simplificar la Fraccion resultante */
    private int mcd(Fraccion frac) { //  20/40 = MCD = 20 

        int aux_num, aux_den, mcd;
        int num = frac.getNumerador();
        int den = frac.getDenominador();
        if (num != 0) {
            if (num < 0) {
                num = -1 * num; //se obtiene el numero positivo
            }
            if (den < 0) {
                den = -1 * den;
            }
            if (num > den) {
                aux_num = num;
                aux_den = den;
            } else {  // = % <
                aux_num = den;
                aux_den = num;
            }
            mcd = aux_den;
            while (aux_den != 0) {
                mcd = aux_den;
                aux_den = aux_num % aux_den; // se obtiene residuo, el ciclo se ejecuta
                //hasta que la division da cero
                aux_num = mcd;
            }
        } else {
            mcd = 1;
        }
        return mcd;
    }

    private Fraccion simplificar(Fraccion f) {
        int mcd = mcd(f);
        f.setNumerador(f.getNumerador() / mcd);
        f.setDenominador(f.getDenominador() / mcd);
        if (f.getNumerador() < 0 && f.getDenominador() < 0) { //ambos son negativos
            f.setNumerador(-1 * f.getNumerador());
            f.setDenominador(-1 * f.getDenominador());
        } else if (f.getNumerador() >= 0 && f.getDenominador() < 0) {
            f.setNumerador(-1 * f.getNumerador());
            f.setDenominador(-1 * f.getDenominador());
        }
        return f;
    }

    public double toDecimal() {
        return (double) this.getNumerador() / (double) this.getDenominador();
    }

    public int posicionBarra(String cadena) {
        return cadena.indexOf("/");
    }

    public Fraccion deTablaFraccion(String fraccionString) {
        Fraccion newFraccion;
        int posicionBarra = posicionBarra(fraccionString);
        String numerador = "", denominador = "";
        if (posicionBarra == -1) {
            newFraccion = new Fraccion(Integer.parseInt(fraccionString), 1);// ?/1
            return newFraccion;
        } else {
            for (int i = 0; i < posicionBarra; i++) {
                numerador += fraccionString.charAt(i);
            }
            for (int j = posicionBarra + 1; j < fraccionString.length(); j++) {
                denominador += fraccionString.charAt(j);
            }
            newFraccion = new Fraccion(Integer.parseInt(numerador), Integer.parseInt(denominador));
            return newFraccion;
        }
    }

    private int getPuntoDecimal(String decimalStr) {
        return decimalStr.indexOf(".");
    }

    private String getPartInt(String val) {
        int i = getPuntoDecimal(val);
        return val.substring(0, i);
    }

    private String getPartDecimal(String val) {
        int i = getPuntoDecimal(val);
        return val.substring(i + 1, val.length());
    }

    private int factorMultiplicador(int longitud) {
        String factorMultiplicador = "1";
        for (int i = 0; i < longitud; i++) {
            factorMultiplicador += "0";
        }
        return Integer.parseInt(factorMultiplicador);

    }

    public Fraccion toFraccion(double decimal) {
        Fraccion newFraccion;
        String partInt = getPartInt(String.valueOf(decimal));
        String partDecimal = getPartDecimal(String.valueOf(decimal));
        int num = Integer.parseInt(partInt);
        int den = Integer.parseInt(partDecimal);
        int factorMultiplicador = factorMultiplicador(partDecimal.length());
        if (num == 0) {
            newFraccion = new Fraccion(den, factorMultiplicador);
        } else {
            Fraccion f1 = new Fraccion(num, 1);
            System.out.println("f1 Numerador: " + f1.getNumerador());
            System.out.println("f1 Denominador: " + f1.getDenominador());
            Fraccion f2 = new Fraccion(den, factorMultiplicador);
            System.out.println("f1 Numerador: " + f2.getNumerador());
            System.out.println("f1 Denominador: " + f2.getDenominador());

            newFraccion = f1.sumar(f2);
        }
        return newFraccion;
    }

    /*Funcion toString para devolver el resultado obtenido de las operaciones*/
    @Override
    public String toString() {
        if (this.getNumerador() != 0) {
            simplificar(this);
            if (this.getDenominador() == 1) {
                return String.valueOf(getNumerador());
            } else {
                return getNumerador() + "/" + getDenominador();
            }
        } else {
            return "0"; // "El denominador debe ser distinto de 0";
        }
    }
   
}
//https://uh-tis.blogspot.com/2019/03/como-convertir-numero-decimal-a-fraccion-java.html

