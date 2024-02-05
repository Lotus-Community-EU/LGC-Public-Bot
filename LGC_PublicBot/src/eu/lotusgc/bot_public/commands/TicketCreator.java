package eu.lotusgc.bot_public.commands;

import eu.lotusgc.bot_public.main.Main;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class TicketCreator extends ListenerAdapter{
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if(event.getName().equals("ticket")) {
			TextInput title = TextInput.create("title", "Title", TextInputStyle.SHORT)
					.setPlaceholder("What is it about?")
					.setMinLength(10)
					.setMaxLength(100)
					.build();
			TextInput main = TextInput.create("body", "Detail", TextInputStyle.PARAGRAPH)
					.setPlaceholder("Elaborate your issue as good as you can. (You may attach files later)")
					.setRequiredRange(50, 750)
					.build();
			
			Modal modal = Modal.create("ticket", "Ticket")
					.addComponents(ActionRow.of(title), ActionRow.of(main))
					.build();
			event.replyModal(modal).queue();
		}
	}
	
	@Override
	public void onModalInteraction(ModalInteractionEvent event) {
		if(event.getModalId().equals("ticket")) {
			String title = event.getValue("title").getAsString();
			String main = event.getValue("body").getAsString();
			event.reply("Thank you for creating a ticket - we'll get back to you as soon as possible!").setEphemeral(true).queue();
			Main.logger.info("Title=" + title + ", Body=" + main);
		}
	}

}
