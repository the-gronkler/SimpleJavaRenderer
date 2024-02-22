package swing_test;

// 3x3 matrix of type double
public record Matrix3D(double... values) {
    public Matrix3D {
        if (values.length != 9)
            throw new IllegalArgumentException("expected array length of 9");
    }
    public Matrix3D multiply(Matrix3D other) {
        double[] res = new double[9],
                m2 = other.values();

        // row and col are indices in result matrix
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 3; col++)
                //here calculate the value
                for (int i = 0; i < 3; i++)
                    res[3 * row + col] +=
                            values[row * 3 + i] * m2[i * 3 + col];
        //math is hard

        return new Matrix3D(res);
    }
}
