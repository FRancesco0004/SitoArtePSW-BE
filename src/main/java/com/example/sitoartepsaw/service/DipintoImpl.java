package com.example.sitoartepsaw.service;

import com.example.sitoartepsaw.enums.TipoOpera;
import org.springframework.stereotype.Component;

@Component
public class DipintoImpl implements OggettoInterface {

    @Override
    public TipoOpera getTipoOpera() {
        return TipoOpera.DIPINTO;
    }
}
