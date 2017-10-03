package com.mycompany.mystaff.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.mycompany.mystaff.security.jwt.JWTConfigurer;
import com.mycompany.mystaff.security.jwt.TokenProvider;
import com.mycompany.mystaff.service.CategoryService;
import com.mycompany.mystaff.service.dto.CategoryDTO;
import com.mycompany.mystaff.service.util.ResolveTokenUtil;
import com.mycompany.mystaff.web.rest.util.HeaderUtil;

import io.github.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing Category.
 */
@RestController
@RequestMapping("/api")
public class CategoryResource {

  private final Logger log = LoggerFactory.getLogger(CategoryResource.class);

  private static final String ENTITY_NAME = "category";

  private final HttpServletRequest request;

  private final CategoryService categoryService;

  private final TokenProvider tokenProvider;

  public CategoryResource(CategoryService categoryService, HttpServletRequest request, TokenProvider tokenProvider) {
    this.categoryService = categoryService;
    this.request = request;
    this.tokenProvider = tokenProvider;
  }

  /**
   * POST /categories : Create a new category.
   *
   * @param categoryDTO the categoryDTO to create
   * @return the ResponseEntity with status 201 (Created) and with body the new categoryDTO, or with
   *         status 400 (Bad Request) if the category has already an ID
   * @throws URISyntaxException if the Location URI syntax is incorrect
   */
  @PostMapping("/categories")
  @Timed
  public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) throws URISyntaxException {
    log.debug("REST request to save Category : {}", categoryDTO);

    if (categoryDTO.getId() != null) {
      return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new category cannot already have an ID")).body(null);
    }

    final Long companyId = tokenProvider.getCompanyId(ResolveTokenUtil.resolveToken(request.getHeader(JWTConfigurer.AUTHORIZATION_HEADER)));

    CategoryDTO result = categoryService.save(categoryDTO, companyId);
    return ResponseEntity.created(new URI("/api/categories/" + result.getId())).headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString())).body(result);
  }

  /**
   * PUT /categories : Updates an existing category.
   *
   * @param categoryDTO the categoryDTO to update
   * @return the ResponseEntity with status 200 (OK) and with body the updated categoryDTO, or with
   *         status 400 (Bad Request) if the categoryDTO is not valid, or with status 500 (Internal
   *         Server Error) if the categoryDTO couldn't be updated
   * @throws URISyntaxException if the Location URI syntax is incorrect
   */
  @PutMapping("/categories")
  @Timed
  public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO) throws URISyntaxException {
    log.debug("REST request to update Category : {}", categoryDTO);

    if (categoryDTO.getId() == null) {
      return createCategory(categoryDTO);
    }

    final Long companyId = tokenProvider.getCompanyId(ResolveTokenUtil.resolveToken(request.getHeader(JWTConfigurer.AUTHORIZATION_HEADER)));

    CategoryDTO result = categoryService.save(categoryDTO, companyId);
    return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, categoryDTO.getId().toString())).body(result);
  }

  /**
   * GET /categories : get all the categories.
   *
   * @return the ResponseEntity with status 200 (OK) and the list of categories in body
   */
  @GetMapping("/categories")
  @Timed
  public List<CategoryDTO> getAllCategories() {
    log.debug("REST request to get all Categories");

    final Long companyId = tokenProvider.getCompanyId(ResolveTokenUtil.resolveToken(request.getHeader(JWTConfigurer.AUTHORIZATION_HEADER)));
    return categoryService.findByCompanyId(companyId);
  }

  /**
   * GET /categories/:id : get the "id" category.
   *
   * @param id the id of the categoryDTO to retrieve
   * @return the ResponseEntity with status 200 (OK) and with body the categoryDTO, or with status
   *         404 (Not Found)
   */
  @GetMapping("/categories/{id}")
  @Timed
  public ResponseEntity<CategoryDTO> getCategory(@PathVariable Long id) {
    log.debug("REST request to get Category : {}", id);

    final Long companyId = tokenProvider.getCompanyId(ResolveTokenUtil.resolveToken(request.getHeader(JWTConfigurer.AUTHORIZATION_HEADER)));
    CategoryDTO categoryDTO = categoryService.findByIdAndCompanyID(id, companyId);
    return ResponseUtil.wrapOrNotFound(Optional.ofNullable(categoryDTO));
  }

  /**
   * DELETE /categories/:id : delete the "id" category.
   *
   * @param id the id of the categoryDTO to delete
   * @return the ResponseEntity with status 200 (OK)
   */
  @DeleteMapping("/categories/{id}")
  @Timed
  public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
    log.debug("REST request to delete Category : {}", id);

    final Long companyId = tokenProvider.getCompanyId(ResolveTokenUtil.resolveToken(request.getHeader(JWTConfigurer.AUTHORIZATION_HEADER)));
    categoryService.deleteByIdAndCompanyId(id, companyId);
    return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
  }

  /**
   * SEARCH /_search/categories?query=:query : search for the category corresponding to the query.
   *
   * @param query the query of the category search
   * @return the result of the search
   */
  @GetMapping("/_search/categories")
  @Timed
  public List<CategoryDTO> searchCategories(@RequestParam String query) {
    log.debug("REST request to search Categories for query {}", query);

    final Long companyId = tokenProvider.getCompanyId(ResolveTokenUtil.resolveToken(request.getHeader(JWTConfigurer.AUTHORIZATION_HEADER)));
    return categoryService.search(query, companyId);
  }

}
