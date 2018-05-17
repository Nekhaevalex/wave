import java.awt.image.BufferedImage;

class MathProcessor {

    static double[] approximate(BufferedImage img, int extent) {
        Matrix a = new Matrix(extent);
        Matrix b = new Matrix(1, extent);
        double[] result = new double[extent];
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                if ((img.getRGB(i, j) & 0x000000ff) < 255) {
                    for (int x = 0; x < extent; x++) {
                        for (int y = 0; y < extent; y++) {
                            a.addToCell(x, y, Math.pow(i, x + y));
                            b.addToCell(0, y, j * Math.pow(i, y));
                        }
                    }
                }
            }
        }
        //Solve here
        double d = a.determinant();
        for (int i = 0; i < extent; i++) {
            Matrix w = new Matrix(a);
            w.insert(i, b);
            result[i] = w.determinant() / d;
        }
        return result;
    }
}
