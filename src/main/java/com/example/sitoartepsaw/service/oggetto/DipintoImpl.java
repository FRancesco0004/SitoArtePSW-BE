package com.example.sitoartepsaw.service.oggetto;

import com.example.sitoartepsaw.enums.TipoOpera;

public class DipintoImpl implements OggettoInterface {

    @Override
    public TipoOpera getTipoOpera() {
        return TipoOpera.DIPINTO;
    }
}
