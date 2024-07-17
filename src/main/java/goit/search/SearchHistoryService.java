package goit.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
public class SearchHistoryService {


    @Autowired
    private final SearchHistoryRepository searchHistoryRepository;

    public SearchHistoryService(SearchHistoryRepository searchHistoryRepository) {
        this.searchHistoryRepository = searchHistoryRepository;
    }


    public List<SearchHistory> getSearchHistoryByUserId(UUID userId) {
        return searchHistoryRepository.findByUserId(userId);
    }


    public void saveSearchHistory(SearchHistory searchHistory) {
        searchHistoryRepository.save(searchHistory);
    }


}

