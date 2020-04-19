package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;

@RestController
public class TournamentController {

    @Autowired
    TournamentService tournamentService;

    @GetMapping("/executions/{executionId}/tournaments/open")
    @PreAuthorize("hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<TournamentDto> findOpenTournaments(@PathVariable int executionId) {
        return tournamentService.getOpenTournaments(executionId);
    }

    @PostMapping("/executions/{executionId}/tournaments/")
    @PreAuthorize("hasRole('ROLE_STUDENT') && hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public TournamentDto createTournament(Principal principal,
                                          @PathVariable int executionId,
                                          @Valid @RequestBody TournamentDto tournamentDto) {
        User user = (User) ((Authentication) principal).getPrincipal();
        checkUserAuth(user);

        formatDates(tournamentDto);
        return tournamentService.createTournament(user.getId(), executionId, tournamentDto);
    }

    @PostMapping("/tournaments/{tournamentId}/signUp")
    @PreAuthorize("hasRole('ROLE_STUDENT') && hasPermission(#tournamentId, 'TOURNAMENT.ACCESS')")
    public ResponseEntity signUpForTournament(Principal principal,@PathVariable int tournamentId) {

        User user = (User) ((Authentication) principal).getPrincipal();
        checkUserAuth(user);

        tournamentService.signUp(user.getId(), tournamentId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/tournaments/{tournamentId}/cancel")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#tournamentId, 'TOURNAMENT.CANCEL')")
    public ResponseEntity cancelTournament(@PathVariable int tournamentId) {
        tournamentService.cancelTournament(tournamentId);
        return ResponseEntity.ok().build();
    }

    private void checkUserAuth(User user) {
        if(user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }
    }

    private void formatDates(TournamentDto tournament) {
        tournament.setStartingDate(DateHandler.formatFromRequest(tournament.getStartingDate()));
        tournament.setConclusionDate(DateHandler.formatFromRequest(tournament.getConclusionDate()));
    }
}