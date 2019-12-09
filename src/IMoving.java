/*
Autor: Filip Przygoński
*/

/**
 * interfejs odpowiadający za poruszanie się obiektów
 */
public interface IMoving {

    /**
     * przesuwa element
     *
     * @param dx o ile pikseli w prawo
     * @param dy o ile pikseli w górę
     */
    void move(int dx, int dy);
}
