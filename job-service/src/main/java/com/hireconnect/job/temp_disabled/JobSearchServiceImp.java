package com.hireconnect.job.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.hireconnect.job.document.JobDocument;
import com.hireconnect.job.dto.JobRequest;
import com.hireconnect.job.dto.JobResponse;
import com.hireconnect.job.entity.Job;
import com.hireconnect.job.enums.JobStatus;
import com.hireconnect.job.repository.JobSearchRepository;
import com.hireconnect.job.util.JobMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobSearchServiceImp implements JobSearchService {

    private final JobSearchRepository jobSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final JobMapper jobMapper;

    @Override
    public void indexJob(Job job) {
        try {
            JobDocument document = mapToDocument(job);
            jobSearchRepository.save(document);
            log.info("Job indexed successfully: {}", job.getJobId());
        } catch (Exception e) {
            log.error("Failed to index job: {}", job.getJobId(), e);
        }
    }

    @Override
    public void indexJob(JobResponse jobResponse) {
        try {
            JobDocument document = mapResponseToDocument(jobResponse);
            jobSearchRepository.save(document);
            log.info("Job indexed successfully: {}", jobResponse.getJobId());
        } catch (Exception e) {
            log.error("Failed to index job: {}", jobResponse.getJobId(), e);
        }
    }

    @Override
    public void deleteJobIndex(Long jobId) {
        try {
            jobSearchRepository.deleteById(jobId);
            log.info("Job deleted from index: {}", jobId);
        } catch (Exception e) {
            log.error("Failed to delete job from index: {}", jobId, e);
        }
    }

    @Override
    public void updateJobIndex(Long jobId, JobRequest request) {
        try {
            JobDocument document = JobDocument.builder()
                    .jobId(jobId)
                    .title(request.getTitle())
                    .category(request.getCategory())
                    .type(request.getType())
                    .location(request.getLocation())
                    .minSalary(request.getMinSalary())
                    .maxSalary(request.getMaxSalary())
                    .description(request.getDescription())
                    .skills(request.getSkills())
                    .experienceRequired(request.getExperienceRequired())
                    .postedBy(request.getPostedBy())
                    .status(request.getStatus())
                    .build();
            jobSearchRepository.save(document);
            log.info("Job updated in index: {}", jobId);
        } catch (Exception e) {
            log.error("Failed to update job in index: {}", jobId, e);
        }
    }

    @Override
    public List<JobResponse> searchJobs(String keyword) {
        List<JobDocument> results;
        if (keyword == null || keyword.isBlank()) {
            results = jobSearchRepository.findByStatus(JobStatus.OPEN, Pageable.unpaged()).getContent();
        } else {
            results = jobSearchRepository.searchByKeyword(keyword);
        }
        return results.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobResponse> searchJobs(String title, String location, String category,
                                         Double minSalary, Double maxSalary, Integer experience) {

        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();
        boolQueryBuilder.must(QueryBuilders.term(t -> t.field("status").value("OPEN")));

        if (title != null && !title.isBlank()) {
            boolQueryBuilder.must(QueryBuilders.multiMatch(m -> m
                    .query(title)
                    .fields("title^3", "description", "skills")));
        }

        if (location != null && !location.isBlank()) {
            boolQueryBuilder.must(QueryBuilders.match(m -> m.field("location").query(location)));
        }

        if (category != null && !category.isBlank()) {
            boolQueryBuilder.must(QueryBuilders.term(t -> t.field("category").value(category)));
        }

        if (minSalary != null) {
            boolQueryBuilder.must(QueryBuilders.range(r -> r
                    .number(n -> n.field("minSalary").gte(minSalary))));
        }

        if (maxSalary != null) {
            boolQueryBuilder.must(QueryBuilders.range(r -> r
                    .number(n -> n.field("maxSalary").lte(maxSalary))));
        }

        if (experience != null) {
            boolQueryBuilder.must(QueryBuilders.range(r -> r
                    .number(n -> n.field("experienceRequired").lte(experience.doubleValue()))));
        }

        Query query = boolQueryBuilder.build()._toQuery();
        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(query)
                .build();

        SearchHits<JobDocument> searchHits = elasticsearchOperations.search(nativeQuery, JobDocument.class);

        return searchHits.getSearchHits().stream()
                .map(hit -> mapToResponse(hit.getContent()))
                .collect(Collectors.toList());
    }

    @Override
    public List<JobResponse> findByLocation(String location) {
        return jobSearchRepository.findByLocation(location).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobResponse> findByCategory(String category) {
        return jobSearchRepository.findByCategoryAndStatus(category, JobStatus.OPEN).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobResponse> findBySalaryRange(Double minSalary, Double maxSalary) {
        return jobSearchRepository.findBySalaryRange(minSalary, maxSalary).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobResponse> findByMinimumExperience(Integer experience) {
        return jobSearchRepository.findByMinimumExperience(experience).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<JobResponse> findAllOpenJobs(Pageable pageable) {
        return jobSearchRepository.findByStatus(JobStatus.OPEN, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public List<JobResponse> advancedSearch(String query, List<String> skills,
                                             String location, Double minSalary, Double maxSalary) {
        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();
        boolQueryBuilder.must(QueryBuilders.term(t -> t.field("status").value("OPEN")));

        if (query != null && !query.isBlank()) {
            boolQueryBuilder.should(QueryBuilders.multiMatch(m -> m
                    .query(query)
                    .fields("title^3", "description^2", "skills")));
        }

        if (skills != null && !skills.isEmpty()) {
            for (String skill : skills) {
                boolQueryBuilder.should(QueryBuilders.term(t -> t.field("skills").value(skill)));
            }
        }

        if (location != null && !location.isBlank()) {
            boolQueryBuilder.must(QueryBuilders.match(m -> m.field("location").query(location)));
        }

        if (minSalary != null) {
            boolQueryBuilder.must(QueryBuilders.range(r -> r
                    .number(n -> n.field("minSalary").gte(minSalary))));
        }

        if (maxSalary != null) {
            boolQueryBuilder.must(QueryBuilders.range(r -> r
                    .number(n -> n.field("maxSalary").lte(maxSalary))));
        }

        Query esQuery = boolQueryBuilder.build()._toQuery();
        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(esQuery)
                .build();

        SearchHits<JobDocument> searchHits = elasticsearchOperations.search(nativeQuery, JobDocument.class);

        return searchHits.getSearchHits().stream()
                .map(hit -> mapToResponse(hit.getContent()))
                .collect(Collectors.toList());
    }

    private JobDocument mapToDocument(Job job) {
        return JobDocument.builder()
                .jobId(job.getJobId())
                .title(job.getTitle())
                .category(job.getCategory())
                .type(job.getType())
                .location(job.getLocation())
                .minSalary(job.getMinSalary())
                .maxSalary(job.getMaxSalary())
                .description(job.getDescription())
                .skills(job.getSkills())
                .experienceRequired(job.getExperienceRequired())
                .postedBy(job.getPostedBy())
                .status(job.getStatus())
                .postedAt(job.getPostedAt())
                .viewCount(job.getViewCount() != null ? job.getViewCount() : 0L)
                .build();
    }

    private JobDocument mapResponseToDocument(JobResponse response) {
        return JobDocument.builder()
                .jobId(response.getJobId())
                .title(response.getTitle())
                .category(response.getCategory())
                .type(response.getType())
                .location(response.getLocation())
                .minSalary(response.getMinSalary())
                .maxSalary(response.getMaxSalary())
                .description(response.getDescription())
                .skills(response.getSkills())
                .experienceRequired(response.getExperienceRequired())
                .postedBy(response.getPostedBy())
                .status(response.getStatus())
                .postedAt(response.getPostedAt())
                .build();
    }

    private JobResponse mapToResponse(JobDocument document) {
        return JobResponse.builder()
                .jobId(document.getJobId())
                .title(document.getTitle())
                .category(document.getCategory())
                .type(document.getType())
                .location(document.getLocation())
                .minSalary(document.getMinSalary())
                .maxSalary(document.getMaxSalary())
                .description(document.getDescription())
                .skills(document.getSkills())
                .experienceRequired(document.getExperienceRequired())
                .postedBy(document.getPostedBy())
                .status(document.getStatus())
                .postedAt(document.getPostedAt())
                .build();
    }
}
