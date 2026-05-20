package com.example.sitoartepsaw.service;

import com.example.sitoartepsaw.enums.TipoOpera;

public class DipintoImpl implements OggettoInterface {

    @Override
    public TipoOpera getTipoOpera() {
        return TipoOpera.DIPINTO;
    }
}
