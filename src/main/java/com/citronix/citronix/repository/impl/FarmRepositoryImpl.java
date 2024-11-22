package com.citronix.citronix.repository.impl;

import com.citronix.citronix.entity.Farm;
import com.citronix.citronix.repository.FarmRepository;
import com.citronix.citronix.repository.FarmSearchRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;

public class FarmRepositoryImpl implements FarmSearchRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Farm> findFarmMultiCriteriaSearch(String query) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Farm> cq = cb.createQuery(Farm.class);
        Root<Farm> farm = cq.from(Farm.class);
        List<Predicate> predicates = new ArrayList<>();

        if (query != null && !query.isEmpty()) {
            Predicate namePredicate = cb.like(cb.lower(farm.get("name")), "%" + query.toLowerCase() + "%");
            predicates.add(namePredicate);
        }

        if (query != null && !query.isEmpty()) {
            Predicate locationPredicate = cb.like(cb.lower(farm.get("location")), "%" + query.toLowerCase() + "%");
            predicates.add(locationPredicate);
        }


        Predicate areaPredicate = null;
        if (query != null && !query.isEmpty()) {
            try {
                Double areaValue = Double.parseDouble(query);
                areaPredicate = cb.equal(farm.get("area"), areaValue);
                predicates.add(areaPredicate);
            } catch (NumberFormatException e) {
                areaPredicate = cb.disjunction();
            }
        }

        if (predicates.isEmpty()) {
            return new ArrayList<>();
        }

        cq.where(cb.or(predicates.toArray(new Predicate[0])));


        TypedQuery<Farm> queryResult = em.createQuery(cq);
        return queryResult.getResultList();
    }

}
