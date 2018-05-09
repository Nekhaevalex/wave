public class Matrix {
    private double[][] value;

    public void set(int i, int j, double value) {
        this.value[i][j] = value;
    }

    public double get(int i, int j) {
        return value[i][j];
    }

    public void add(Matrix m) throws Error {
        if (this.getWidth() == m.getWidth() && this.getHeight() == m.getHeight()) {
            for (int i = 0; i < this.getWidth(); i++) {
                for (int j = 0; j < this.getHeight(); j++) {
                    this.value[i][j] += m.value[i][j];
                }
            }
        } else {
            throw new Error();
        }
    }

    //    public void multiply (Matrix m) throws Error {
//        if (this.getWidth() == m.getHeight() && this.getHeight() == m.getWidth()) {
//            for (int i = 0; i<this.getWidth(); i++) {
//
//            }
//        } else {
//            throw new Error();
//        }
//    }
    public void addToCell(int x, int y, double value) {
        this.value[x][y] += value;
    }

    public double determinant() throws Error {
        if (this.getHeight() != this.getWidth()) {
            throw new Error();
        }
        double result = 0;
        Matrix temporary;
        if (this.getWidth() == 1) {
            result = this.get(0, 0);
            return result;
        }
        if (this.getWidth() == 2) {
            result = (this.get(0, 0) * this.get(1, 1)) - (this.get(0, 1) * this.get(1, 0));
            return result;
        }
        for (int i = 0; i < this.getWidth(); i++) {
            temporary = new Matrix(this.getWidth() - 1);
            for (int j = 1; j < this.getWidth(); j++) {
                for (int k = 0; k < this.getHeight(); k++) {
                    if (k < i) {
                        temporary.set(j - 1, k, this.get(j, k));
                    } else if (k > i) {
                        temporary.set(j - 1, k - 1, this.get(j, k));
                    }
                }
            }
            result += this.get(i, 0) * Math.pow(-1, (double) i) * temporary.determinant();
        }
        return result;
    }

    public void insert(int x, Matrix toInsert) {
        for (int i = 0; i < toInsert.getHeight(); i++) {
            this.set(x, i, toInsert.get(0, i));
        }
    }

    private int getWidth() {
        return this.value.length;
    }

    private int getHeight() {
        return this.value[0].length;
    }

    public void copy(Matrix toCopy) {
        for (int i = 0; i < toCopy.getWidth(); i++) {
            for (int j = 0; j < toCopy.getHeight(); j++) {
                this.set(i, j, toCopy.get(i, j));
            }
        }
    }

    Matrix(int size) {
        this.value = new double[size][size];
    }

    Matrix(int x, int y) {
        this.value = new double[x][y];
    }

    Matrix(Matrix toCopy) {
        this.value = new double[toCopy.getWidth()][toCopy.getHeight()];
        this.copy(toCopy);
    }
}
