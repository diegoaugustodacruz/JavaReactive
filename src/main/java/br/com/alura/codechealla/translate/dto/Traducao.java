package br.com.alura.codechealla.translate.dto;

import java.util.List;

public record Traducao(

        List<Texto> translations

) {

    public String getTexto(){
        return translations.get(0).text();
    }
}
