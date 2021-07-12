//Taahir Bhorat
public class WaterThread {

    volatile int Depth = 0;
    int I, J;

    WaterThread(int positionI, int PositionJ) {
        I = positionI;
        J = PositionJ;
    }
}
