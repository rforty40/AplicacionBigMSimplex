package controlador;

public class ClaseM {

    private Fraccion coeficiente;
    private String letraM;

    public ClaseM() {
    }

    public ClaseM(Fraccion coeficiente, String letraM) {
        this.coeficiente = coeficiente;
        this.letraM = letraM;
    }

    public Fraccion getCoeficiente() {
        return coeficiente;
    }

    public void setCoeficiente(Fraccion coeficiente) {
        this.coeficiente = coeficiente;
    }

    public String getLetraM() {
        return letraM;
    }

    public void setLetraM(String letraM) {
        this.letraM = letraM;
    }

    @Override
    public String toString() {
        if ("0".equals(coeficiente.toString())) {
            return "0";
        } else {
            return String.valueOf(coeficiente + "" + letraM);

        }
    }

    public boolean esM(String m) {
        int hayM = m.indexOf("M");
        if (hayM == -1) {
            return false;
        } else {
            return true;
        }
    }

    public ClaseM obtenerM(String m) {
        ClaseM clase = new ClaseM();
        Fraccion frac = new Fraccion();
        String zjstring = m.substring(0, m.length() - 1);
        clase.setCoeficiente(frac.deTablaFraccion(zjstring));
        clase.setLetraM("M");
        clase.toString();
        return clase;
    }

    public String obtenerValorSinM(String M) {
        String valorSinM = "";
        Fraccion fracOperacion = new Fraccion();
        Fraccion frac = new Fraccion();
        int hayM = M.indexOf('M');
        if (hayM == -1) {
            valorSinM = M;
        } else {
            frac = frac.deTablaFraccion(M.substring(0, hayM));
            frac = frac.multiplicar(new Fraccion(10, 1));
            if (hayM != M.length() - 1) {
                fracOperacion = fracOperacion.deTablaFraccion(M.substring(hayM + 2, M.length()));
                if (M.charAt(hayM + 1) == '+') {
                    frac = frac.sumar(fracOperacion);
                } else {
                    frac = frac.restar(fracOperacion);
                }
            }
            
            valorSinM=frac.toString();
        }
        return valorSinM;
    }

    public ClaseM sumarM(ClaseM varM) {
        return new ClaseM(getCoeficiente().sumar(varM.getCoeficiente()), "M");
    }

    public ClaseM restarM(ClaseM varM) {
        return new ClaseM(getCoeficiente().restar(varM.getCoeficiente()), "M");
    }

}
