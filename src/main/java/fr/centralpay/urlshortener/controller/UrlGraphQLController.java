package fr.centralpay.urlshortener.controller;

import fr.centralpay.urlshortener.model.entity.UrlEntity;
import fr.centralpay.urlshortener.repository.UrlEntityRepository;
import fr.centralpay.urlshortener.service.UrlService;
import fr.centralpay.urlshortener.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@Controller
@RequestMapping(value = Constants.GRAPHQL_MAPPING)
@RequiredArgsConstructor
public class UrlGraphQLController {

    private final UrlEntityRepository urlEntityRepository;
    private final UrlService urlService;

    @QueryMapping
    public List<UrlEntity> findAllUrl() {
        return urlEntityRepository.findAll();
    }

    @QueryMapping
    public UrlEntity urlByHash(@Argument String id) {
        var optUrl = urlEntityRepository.findById(id);
        return optUrl.orElse(null);
    }

    @MutationMapping
    public UrlEntity saveUrl(@Argument String url) {
        return urlService.createNewOrFindUrlEntity(url);
    }
}
