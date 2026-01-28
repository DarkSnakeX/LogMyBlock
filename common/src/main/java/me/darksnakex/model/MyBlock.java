package me.darksnakex.model;

import net.minecraft.core.BlockPos;

import java.util.Objects;

public class MyBlock {


    private String nombre;
    private BlockPos pos;

    public MyBlock(String nombre, BlockPos pos) {
        this.nombre = nombre;
        this.pos = pos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyBlock myBlock = (MyBlock) o;
        return Objects.equals(nombre, myBlock.nombre) && Objects.equals(pos, myBlock.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre, pos);
    }
}
