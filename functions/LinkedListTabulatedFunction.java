package functions;

public class LinkedListTabulatedFunction implements TabulatedFunction {
    // внутренний класс для узлов списка
    private class FunctionNode {
        FunctionPoint point;
        FunctionNode prev;
        FunctionNode next;

        FunctionNode(FunctionPoint point) {
            this.point = point;
        }
    }

    private FunctionNode head; // голова списка
    private int pointsCount;

    // конструкторы
    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX) throw new IllegalArgumentException("Left border >= right border");
        if (pointsCount < 2) throw new IllegalArgumentException("Less than 2 points");

        initHead();
        double xStep = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * xStep;
            double y = 0;
            addNodeToTail().point = new FunctionPoint(x, y);
        }
        this.pointsCount = pointsCount;
    }

    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values) {
        if (leftX >= rightX) throw new IllegalArgumentException("Left border >= right border");
        if (values.length < 2) throw new IllegalArgumentException("Less than 2 points");

        initHead();
        double xStep = (rightX - leftX) / (values.length - 1);
        for (int i = 0; i < values.length; i++) {
            double x = leftX + i * xStep;
            addNodeToTail().point = new FunctionPoint(x, values[i]);
        }
        this.pointsCount = values.length;
    }

    // инициализация головы списка
    private void initHead() {
        head = new FunctionNode(null);
        head.next = head;
        head.prev = head;
        pointsCount = 0;
    }

    // добавление узла в конец
    private FunctionNode addNodeToTail() {
        FunctionNode newNode = new FunctionNode(null);
        FunctionNode tail = head.prev;

        tail.next = newNode;
        newNode.prev = tail;
        newNode.next = head;
        head.prev = newNode;

        pointsCount++;
        return newNode;
    }

    // добавление узла по индексу
    private FunctionNode addNodeByIndex(int index) {
        if (index < 0 || index > pointsCount)
            throw new FunctionPointIndexOutOfBoundsException(index);

        if (index == pointsCount) {
            return addNodeToTail();
        }

        FunctionNode newNode = new FunctionNode(null);
        FunctionNode target = getNodeByIndex(index);
        FunctionNode prevNode = target.prev;

        prevNode.next = newNode;
        newNode.prev = prevNode;
        newNode.next = target;
        target.prev = newNode;

        pointsCount++;
        return newNode;
    }

    // удаление узла по индексу
    private FunctionNode deleteNodeByIndex(int index) {
        if (index < 0 || index >= pointsCount)
            throw new FunctionPointIndexOutOfBoundsException(index);

        FunctionNode toDelete = getNodeByIndex(index);
        toDelete.prev.next = toDelete.next;
        toDelete.next.prev = toDelete.prev;
        pointsCount--;

        return toDelete;
    }

    // получение узла по индексу
    private FunctionNode getNodeByIndex(int index) {
        if (index < 0 || index >= pointsCount)
            throw new FunctionPointIndexOutOfBoundsException(index);

        FunctionNode current = head.next;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current;
    }

    // реализация методов TabulatedFunction
    public int getPointsCount() { return pointsCount; }

    public double getPointX(int index) {
        return getNodeByIndex(index).point.getX();
    }

    public double getPointY(int index) {
        return getNodeByIndex(index).point.getY();
    }

    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        if (index > 0 && x <= getPointX(index - 1))
            throw new InappropriateFunctionPointException("X must be greater than previous point");
        if (index < pointsCount - 1 && x >= getPointX(index + 1))
            throw new InappropriateFunctionPointException("X must be less than next point");

        getNodeByIndex(index).point.setX(x);
    }

    public void setPointY(int index, double y) {
        getNodeByIndex(index).point.setY(y);
    }

    public double getLeftDomainBorder() {
        return pointsCount > 0 ? getPointX(0) : Double.NaN;
    }

    public double getRightDomainBorder() {
        return pointsCount > 0 ? getPointX(pointsCount - 1) : Double.NaN;
    }

    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder())
            return Double.NaN;

        for (int i = 0; i < pointsCount - 1; i++) {
            double x1 = getPointX(i);
            double x2 = getPointX(i + 1);
            if (x >= x1 && x <= x2) {
                double y1 = getPointY(i);
                double y2 = getPointY(i + 1);
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
        }
        return Double.NaN;
    }

    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        for (int i = 0; i < pointsCount; i++) {
            if (Math.abs(getPointX(i) - point.getX()) < 1e-10)
                throw new InappropriateFunctionPointException("Point with this X already exists");
        }

        int insertIndex = 0;
        while (insertIndex < pointsCount && getPointX(insertIndex) < point.getX()) {
            insertIndex++;
        }

        FunctionNode newNode = addNodeByIndex(insertIndex);
        newNode.point = new FunctionPoint(point);
    }

    public void deletePoint(int index) {
        if (pointsCount < 3) throw new IllegalStateException("Cannot delete point - less than 3 points left");
        deleteNodeByIndex(index);
    }

    public FunctionPoint getPoint(int index) {
        return new FunctionPoint(getNodeByIndex(index).point);
    }

    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        setPointX(index, point.getX());
        setPointY(index, point.getY());
    }
}