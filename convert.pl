#!/usr/local/bin/perl -w
# Copyright 2016 Yahoo Inc.
# Licensed under the terms of the New-BSD license. Please see LICENSE file in the project root for terms.

# simple script I can pass gc logs through to turn the KB into MB
use strict;

while (<>) {
	chomp;
	my @a=split(/([ (>)-])/,$_);
	for (my $i=0;$i<= $#a;$i+=2) {
		my $k=$a[$i];
		my $s=$a[$i+1];
		if (!defined $s) {
			$s=" ";
		}
		if ($k =~ /([0-9]*)K/) {
			my $v=$1;
			$v= $v / 1024;
			printf ("%2.0fM$s", ${v});
		} else {
			print "${k}$s";
		}
	}
	print "\n";
}