package br.com.rest.controller;

import br.com.domain.dto.*;
import br.com.domain.entity.Estoque;
import br.com.exception.BadRequestException;
import br.com.exception.NotFoundException;
import br.com.service.impl.EstoqueServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/estoque")
public class EstoqueController {

    @Autowired
    private EstoqueServiceImpl service;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EstoqueRetornoDTO created(@RequestBody @Valid EstoqueDTO estoque){
        return service.create(estoque)
                .orElseThrow(()-> new BadRequestException("Estoque já cadastrado"));
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public EstoqueDTO updateEstoque(@RequestBody @Valid EstoquePutDTO dto){
        if (dto.getQuantidade()==null && dto.getPrecoUnitario()==null){
            throw new NotFoundException("O campo quantidade e precoUnitario não podem ser ambos null.");
        }
        // TODO: 16/04/2023  melhorar validação
        return service.updateEstoque(dto)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Mercado ou produto não cadastrado"));
    }

    // TODO: 16/04/2023 fazer endpoint de verificação de produto
    //Codigo de barras
    //quantidade
    //id_mercado
    @GetMapping("/verificar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void verificar(@RequestBody VendaProdutoDTO dto){
        service.verificarVenda(dto);
    }

    @GetMapping("/vender")
    @ResponseStatus(HttpStatus.OK)
    public void vender(@RequestBody ProdutoListDTO dto){
        service.vender(dto);
    }

    @GetMapping
    public List<EstoqueRetornoListDTO> listByMercado(@RequestBody MercadoDTO dto){
        List<Estoque> listEstoque = service.listarPeloNome(dto.getLogin());
        List<EstoqueRetornoListDTO> dtoRetorno = new ArrayList<>();
        for (Estoque estoque : listEstoque) {
            dtoRetorno.add(
                    EstoqueRetornoListDTO
                            .builder()
                            .nomeProduto(estoque.getProduto().getDescricao())
                            .precoUnitario(estoque.getPrecoUnitario())
                            .quantidade(estoque.getQuantidade())
                            .build()
            );
        }
        return dtoRetorno;
    }

    @DeleteMapping
    public void deleteByName(@RequestBody @Valid DeleteEstoqueDTO dto){
        System.out.println(dto.getLogin());
        service.deleteByName(dto.getLogin());
    }

//    @PatchMapping("quantidade")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public List<Estoque> updateQuantidade(@RequestBody AtualizaQuantidadeDTO quantidadeDTO){
//        return service.updateQuantidade(quantidadeDTO);
//    }

}
