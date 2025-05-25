package market.price_comparator.repo;

import market.price_comparator.model.AccountedFiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountedFilesRepository extends JpaRepository<AccountedFiles, String> {
    AccountedFiles findByFileName(String fileName);
}
