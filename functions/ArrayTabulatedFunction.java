package functions;

public class ArrayTabulatedFunction implements TabulatedFunction {
    private FunctionPoint[] points;
    private int pointsCount;

    public ArrayTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница области определения должна быть меньше правой");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек не может быть меньше двух");
        }

        this.pointsCount = pointsCount;
        this.points = new FunctionPoint[pointsCount + 10];

        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            points[i] = new FunctionPoint(x, 0);
        }
    }

    public ArrayTabulatedFunction(double leftX, double rightX, double[] values) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница области определения должна быть меньше правой");
        }
        if (values.length < 2) {
            throw new IllegalArgumentException("Количество точек не может быть меньше двух");
        }

        this.pointsCount = values.length;
        this.points = new FunctionPoint[pointsCount + 10];

        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            points[i] = new FunctionPoint(x, values[i]);
        }
    }

    public double getLeftDomainBorder() {
        return points[0].getX();
    }

    public double getRightDomainBorder() {
        return points[pointsCount - 1].getX();
    }

    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }

        // Бинарный поиск интервала
        int left = 0;
        int right = pointsCount - 1;

        while (left <= right) {
            int mid = (left + right) / 2;
            if (Math.abs(points[mid].getX() - x) < 1e-10) {
                return points[mid].getY();
            } else if (points[mid].getX() < x) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        // Линейная интерполяция
        int index = (points[left].getX() < x) ? left + 1 : left;
        FunctionPoint p1 = points[index - 1];
        FunctionPoint p2 = points[index];

        double k = (p2.getY() - p1.getY()) / (p2.getX() - p1.getX());
        return k * (x - p1.getX()) + p1.getY();
    }

    public int getPointsCount() {
        return pointsCount;
    }

    public FunctionPoint getPoint(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index);
        }
        return new FunctionPoint(points[index]);
    }

    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index);
        }

        if ((index > 0 && point.getX() <= points[index - 1].getX()) ||
                (index < pointsCount - 1 && point.getX() >= points[index + 1].getX())) {
            throw new InappropriateFunctionPointException("Нарушена упорядоченность точек");
        }

        points[index] = new FunctionPoint(point);
    }

    public double getPointX(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index);
        }
        return points[index].getX();
    }

    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index);
        }

        if ((index > 0 && x <= points[index - 1].getX()) ||
                (index < pointsCount - 1 && x >= points[index + 1].getX())) {
            throw new InappropriateFunctionPointException("Нарушена упорядоченность точек");
        }

        points[index].setX(x);
    }

    public double getPointY(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index);
        }
        return points[index].getY();
    }

    public void setPointY(int index, double y) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index);
        }
        points[index].setY(y);
    }

    public void deletePoint(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index);
        }
        if (pointsCount < 3) {
            throw new IllegalStateException("Нельзя удалить точку: меньше 3 точек");
        }

        System.arraycopy(points, index + 1, points, index, pointsCount - index - 1);
        pointsCount--;
    }

    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        // Проверка на дублирование
        for (int i = 0; i < pointsCount; i++) {
            if (Math.abs(points[i].getX() - point.getX()) < 1e-10) {
                throw new InappropriateFunctionPointException("Точка с таким X уже существует");
            }
        }

        // Увеличение массива при необходимости
        if (pointsCount >= points.length) {
            FunctionPoint[] newArray = new FunctionPoint[pointsCount + 10];
            System.arraycopy(points, 0, newArray, 0, pointsCount);
            points = newArray;
        }

        // Поиск позиции для вставки
        int insertIndex = 0;
        while (insertIndex < pointsCount && points[insertIndex].getX() < point.getX()) {
            insertIndex++;
        }

        // Сдвиг элементов
        System.arraycopy(points, insertIndex, points, insertIndex + 1, pointsCount - insertIndex);
        points[insertIndex] = new FunctionPoint(point);
        pointsCount++;
    }
}