package com.gastrosena.inventario.service;

import model.dto.dtoRequest.BienRequestDto;
import model.dto.dtoResponse.BienResponseDto;
import model.entity.Bien;

import java.util.List;
import java.util.UUID;

public interface IBienService {

    // ── CRUD BÁSICO ──────────────────────────────────────────

    BienResponseDto registrar(BienRequestDto dto);
    // Recibe el DTO con los datos del bien, valida que el código
    // no exista previamente, calcula valorConIVA y valorSinIVA
    // automáticamente, guarda en BD y retorna el ResponseDto.

    BienResponseDto actualizar(UUID id, BienRequestDto dto);
    // Busca el bien por ID (solo activos), lanza excepción si no
    // existe, actualiza los campos editables, recalcula valores
    // si cambia el precio o el impuesto, guarda y retorna el ResponseDto.

    BienResponseDto obtenerPorId(UUID id);
    // Busca el bien por ID (solo activos), lanza
    // BienNoEncontradoException si no existe, retorna el
    // ResponseDto completo con todos los campos calculados.

    List<BienResponseDto> listar(
            String nombre,
            String codigo,
            UUID categoriaId,
            Boolean bajoStock
    );
    // Construye filtros dinámicos con Specification según los
    // parámetros recibidos (todos opcionales). Si bajoStock=true
    // filtra solo los que tienen stockActual <= stockMinimo.
    // Retorna la lista de bienes activos que cumplan los filtros.

    // ── ELIMINACIÓN ──────────────────────────────────────────

    void eliminar(UUID id);
    // Busca el bien por ID (solo activos), lanza excepción si no
    // existe, hace eliminación lógica (activo=false) y guarda.
    // No borra el registro físicamente de la BD.

    int eliminarMasivo(List<UUID> ids, String confirmacion);
    // Valida que la palabra de confirmacion sea exactamente
    // "ELIMINAR", lanza ConfirmacionInvalidaException si no
    // coincide. Hace eliminación lógica de todos los IDs
    // encontrados. Retorna el total de bienes eliminados.

    // ── STOCK ────────────────────────────────────────────────

    void verificarStockMinimo(Bien bien);
    // Evalúa si stockActual <= stockMinimo. Si se cumple,
    // actualiza estadoStock a "BAJO_STOCK", si no lo pone en
    // "NORMAL". Se llama internamente después de registrar,
    // actualizar o registrar un movimiento de salida.

    List<BienResponseDto> listarBajoStock();
    // Retorna todos los bienes activos cuyo stockActual sea
    // menor o igual a su stockMinimo. Usado para el panel de
    // alertas y el control visual del inventario.

    // ── IMPORTACIÓN / EXPORTACIÓN ────────────────────────────

//    ImportacionResultadoDto importar(MultipartFile archivo);
    // Detecta si el archivo es Excel o CSV por su extensión.
    // Lee cada fila, construye un BienRequestDto por fila,
    // valida y llama a registrar(). Acumula los errores por
    // número de fila sin detener el proceso. Retorna el
    // resultado con totales de exitosos, fallidos y lista
    // de errores detallados.

    byte[] exportarExcel(List<BienResponseDto> bienes);
    // Recibe la lista de bienes a exportar, construye un
    // XSSFWorkbook con Apache POI con una hoja llamada "Bienes",
    // fila de encabezados en negrita y una fila por bien.
    // Retorna el archivo como arreglo de bytes.

    byte[] exportarPdf(List<BienResponseDto> bienes);
    // Recibe la lista de bienes a exportar, construye un
    // documento PDF con iText con tabla de columnas y una
    // fila por bien. Retorna el archivo como arreglo de bytes.
}