package bsu.rfe.java.group9.lab2.Nebyshinets.varC;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.*;
import javax.swing.JPanel;
@SuppressWarnings("serial")
public class GraphicsDisplay extends JPanel {
    // Список координат точек для построения графика
    private Double[][] graphicsData;
    // Флаговые переменные, задающие правила отображения графика
    private boolean showAxis = true;
    private boolean showMarkers = true;
    // Границы диапазона пространства, подлежащего отображению
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    // Используемый масштаб отображения
    private double scale;
    // Различные стили черчения линий
    private BasicStroke graphicsStroke;
    private BasicStroke axisStroke;
    private BasicStroke markerStroke;
    // Различные шрифты отображения надписей
    private Font axisFont;
    public GraphicsDisplay() {
// Цвет заднего фона области отображения - белый
        setBackground(Color.WHITE);
// Сконструировать необходимые объекты, используемые в рисовании
// Перо для рисования графика
        graphicsStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_ROUND, 10.0f, new float[] {16, 4, 4, 4, 8, 4, 4, 4}, 0.0f);
// Перо для рисования осей координат
        axisStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
// Перо для рисования контуров маркеров
        markerStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
// Шрифт для подписей осей координат
        axisFont = new Font("Serif", Font.BOLD, 36);
    }
// Данный метод вызывается из обработчика элемента меню "Открыть файл с
    // главного окна приложения в случае успешной загрузки данных
    public void showGraphics(Double[][] graphicsData) {
// Сохранить массив точек во внутреннем поле класса
        this.graphicsData = graphicsData;
// Запросить перерисовку компонента, т.е. неявно вызвать
        repaint();
    }
    // Методы-модификаторы для изменения параметров отображения графика
// Изменение любого параметра приводит к перерисовке области
    public void setShowAxis(boolean showAxis) {
        this.showAxis = showAxis;
        repaint();
    }
    public void setShowMarkers(boolean showMarkers) {
        this.showMarkers = showMarkers;
        repaint();
    }
    // Метод отображения всего компонента, содержащего график
    public void paintComponent(Graphics g) {
        /* Шаг 1 - Вызвать метод предка для заливки области цветом заднего фона
         * Эта функциональность - единственное, что осталось в наследство от
         * paintComponent класса JPanel
         */
        super.paintComponent(g);
// Шаг 2 - Если данные графика не загружены (при показе компонента
        if (graphicsData==null || graphicsData.length==0) return;
// Шаг 3 - Определить минимальное и максимальное значения для
// Это необходимо для определения области пространства, подлежащей
// Еѐ верхний левый угол это (minX, maxY) - правый нижний это
        minX = graphicsData[0][0];
        maxX = graphicsData[graphicsData.length-1][0];
        minY = graphicsData[0][1];
        maxY = minY;
// Найти минимальное и максимальное значение функции
        for (int i = 1; i<graphicsData.length; i++) {
            if (graphicsData[i][1]<minY) {
                minY = graphicsData[i][1];
            }
            if (graphicsData[i][1]>maxY) {
                maxY = graphicsData[i][1];
            }
        }
/* Шаг 4 - Определить (исходя из размеров окна) масштабы по осям X
и Y - сколько пикселов
* приходится на единицу длины по X и по Y
*/
        double scaleX = getSize().getWidth() / (maxX - minX);
        double scaleY = getSize().getHeight() / (maxY - minY);
// Шаг 5 - Чтобы изображение было неискажѐнным - масштаб должен
// Выбираем за основу минимальный
        scale = Math.min(scaleX, scaleY);
// Шаг 6 - корректировка границ отображаемой области согласно
        if (scale==scaleX) {
/* Если за основу был взят масштаб по оси X, значит по оси Y
делений меньше,
* т.е. подлежащий визуализации диапазон по Y будет меньше
высоты окна.
* Значит необходимо добавить делений, сделаем это так:
* 1) Вычислим, сколько делений влезет по Y при выбранном
масштабе - getSize().getHeight()/scale
* 2) Вычтем из этого сколько делений требовалось изначально
* 3) Набросим по половине недостающего расстояния на maxY и
minY
*/
            double yIncrement = (getSize().getHeight()/scale - (maxY -
                    minY))/2;
            maxY += yIncrement;
            minY -= yIncrement;
        }
        if (scale==scaleY) {
// Если за основу был взят масштаб по оси Y, действовать по
            double xIncrement = (getSize().getWidth()/scale - (maxX -
                    minX))/2;
            maxX += xIncrement;
            minX -= xIncrement;
        }
// Шаг 7 - Сохранить текущие настройки холста
        Graphics2D canvas = (Graphics2D) g;
        Stroke oldStroke = canvas.getStroke();
        Color oldColor = canvas.getColor();
        Paint oldPaint = canvas.getPaint();
        Font oldFont = canvas.getFont();
// Шаг 8 - В нужном порядке вызвать методы отображения элементов
// Порядок вызова методов имеет значение, т.к. предыдущий рисунок
// Первыми (если нужно) отрисовываются оси координат.
        if (showAxis) paintAxis(canvas);
// Затем отображается сам график
        paintGraphics(canvas);
// Затем (если нужно) отображаются маркеры точек, по которым
        if (showMarkers) paintMarkers(canvas);
// Шаг 9 - Восстановить старые настройки холста
        canvas.setFont(oldFont);
        canvas.setPaint(oldPaint);
        canvas.setColor(oldColor);
        canvas.setStroke(oldStroke);
    }
    // Отрисовка графика по прочитанным координатам
    protected void paintGraphics(Graphics2D canvas) {
// Выбрать линию для рисования графика
        canvas.setStroke(graphicsStroke);
// Выбрать цвет линии
        canvas.setColor(Color.RED);
/* Будем рисовать линию графика как путь, состоящий из множества
сегментов (GeneralPath)
* Начало пути устанавливается в первую точку графика, после чего
прямой соединяется со
* следующими точками
*/
        GeneralPath graphics = new GeneralPath();
        for (int i=0; i<graphicsData.length; i++) {
// Преобразовать значения (x,y) в точку на экране point
            Point2D.Double point = xyToPoint(graphicsData[i][0],
                    graphicsData[i][1]);
            if (i>0) {
// Не первая итерация цикла - вести линию в точку
                graphics.lineTo(point.getX(), point.getY());
            } else {
// Первая итерация цикла - установить начало пути в
                graphics.moveTo(point.getX(), point.getY());
            }
        }
// Отобразить график
        canvas.draw(graphics);
    }

    protected void paintMarkers(Graphics2D canvas) {
        canvas.setStroke(markerStroke);
        canvas.setColor(Color.RED);
        canvas.setPaint(Color.RED);
        for (Double[] point: graphicsData) {
            GeneralPath marker = composeMarker(point);
            if( (point[1] - point[1].intValue()) < 0.1 || (point[1] - point[1].intValue()) > 0.9  ){
                canvas.setColor(Color.BLUE);
            }

            canvas.draw(marker);
            canvas.setColor(Color.RED);
        }
    }
    // Метод, обеспечивающий отображение осей координат
    protected void paintAxis(Graphics2D canvas) {
        canvas.setStroke(axisStroke);
        canvas.setColor(Color.BLACK);
        canvas.setPaint(Color.BLACK);
        canvas.setFont(axisFont);
        FontRenderContext context = canvas.getFontRenderContext();
        if (minX<=0.0 && maxX>=0.0) {
            canvas.draw(new Line2D.Double(xyToPoint(0, maxY), xyToPoint(0, minY)));
            GeneralPath arrow = new GeneralPath();
            Point2D.Double lineEnd = xyToPoint(0, maxY);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            arrow.lineTo(arrow.getCurrentPoint().getX()+5, arrow.getCurrentPoint().getY()+20);
            arrow.lineTo(arrow.getCurrentPoint().getX()-10, arrow.getCurrentPoint().getY());
            arrow.closePath();
            canvas.draw(arrow);
            canvas.fill(arrow);
            Rectangle2D bounds = axisFont.getStringBounds("y", context);
            Point2D.Double labelPos = xyToPoint(0, maxY);
            canvas.drawString("y", (float)labelPos.getX() + 10, (float)(labelPos.getY() - bounds.getY()));
        }
        if (minY<=0.0 && maxY>=0.0) {
                    canvas.draw(new Line2D.Double(xyToPoint(minX, 0),
                            xyToPoint(maxX, 0)));
            GeneralPath arrow = new GeneralPath();
            Point2D.Double lineEnd = xyToPoint(maxX, 0);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            arrow.lineTo(arrow.getCurrentPoint().getX()-20, arrow.getCurrentPoint().getY()-5);
            arrow.lineTo(arrow.getCurrentPoint().getX(), arrow.getCurrentPoint().getY()+10);
            arrow.closePath();
            canvas.draw(arrow);
            canvas.fill(arrow);
            Rectangle2D bounds = axisFont.getStringBounds("x", context);
            Point2D.Double labelPos = xyToPoint(maxX, 0);
            canvas.drawString("x", (float)(labelPos.getX() -
                    bounds.getWidth() - 10), (float)(labelPos.getY() + bounds.getY()));
        }
    }

    protected Point2D.Double xyToPoint(double x, double y) {
        double deltaX = x - minX;
        double deltaY = maxY - y;
        return new Point2D.Double(deltaX*scale, deltaY*scale);
    }

    protected Point2D.Double shiftPoint(Point2D.Double src, double deltaX,
                                        double deltaY) {
        Point2D.Double dest = new Point2D.Double();
        dest.setLocation(src.getX() + deltaX, src.getY() + deltaY);
        return dest;
    }

    protected GeneralPath composeMarker(Double[] point){
        GeneralPath marker = new GeneralPath();
        Point2D.Double point2d = xyToPoint(point[0],
                point[1]);
        double x_0 = point2d.getX();
        double y_0 = point2d.getY();

        marker.moveTo(x_0 - 5, y_0 - 5);
        marker.lineTo(x_0 - 5, y_0 + 5);
        marker.lineTo(x_0 + 5, y_0 + 5);
        marker.lineTo(x_0 + 5, y_0 - 5);
        marker.lineTo(x_0 - 5, y_0 - 5);
        marker.append(new Line2D.Double(x_0 - 5, y_0 - 5, x_0 + 5, y_0 + 5), true);
        marker.append(new Line2D.Double(x_0 - 5, y_0 + 5, x_0 + 5, y_0 - 5), true);

        return marker;
    };
}

