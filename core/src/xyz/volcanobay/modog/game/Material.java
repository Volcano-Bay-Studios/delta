package xyz.volcanobay.modog.game;

public class Material implements Cloneable {
    public String texture;
    public String type;
    public int amount;
    public Material() {

    }
    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public Material clone() {
        try {
            Material clone = (Material) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
